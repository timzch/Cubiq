package cubiq.processing;

import cubiq.io.DebugOutput;
import cubiq.models.GuiModel;
import org.kociemba.twophase.Search;

import java.util.*;

public class BuildCube implements Observer {

    GuiModel guiModel;
    private List<int[][]> sortedScheme;


    private void buildCube() {
        List<int[][]> inputScheme = guiModel.getColorScheme();
        if (!colorsExistsNineTimes(inputScheme)) {
            System.err.println("WRONG COLOR SCHEME: There are not exactly nine stickers of each color");
            return;
        }

        sortedScheme = sortScheme(inputScheme);
        Combinations combinations = new Combinations();

        // Second side
        // Pages that fit at the top of the white page
        for (int side = 1; side < 5; side++) {
            for (int edge = 0; edge < 4; edge++) {

                int[] edge0 = getEdge(0, 0);
                int[] edge1 = getEdge(side, edge);

                if (edgesCouldBeNeighbours(edge0, edge1))
                    combinations.addNewCombination(side, edge);
            }
        }

        int step = 0;

        // Third side
        // Pages that fit the partner of white at the top and right onto the white page
        for (int i = 0; i < combinations.totalStepCombinations(step); i++) {

            int[] edge0 = getEdge(combinations.getSide(step, i, 0), nextEdgeCounterClockWise(combinations.getEdge(step, i, 0)));
            int[] edge1 = getEdge(0, 1);

            for (int side = 1; side < 5; side++) {
                if (side == combinations.getSide(step, i, 0)) continue;

                for (int edge = 0; edge < 4; edge++) {
                    int[] edge2 = getEdge(side, edge);
                    int[] edge3 = getEdge(side, nextEdgeClockWise(edge));

                    if (edgesCouldBeNeighbours(edge0, edge3) && edgesCouldBeNeighbours(edge1, edge2))
                        combinations.extendCombination(step, i, side, edge);
                }
            }
        }

        step = 1;

        // Fourth side
        for (int i = 0; i < combinations.totalStepCombinations(step); i++) {

            int[] edge0 = getEdge(combinations.getSide(step, i, 1), nextEdgeCounterClockWise(combinations.getEdge(step, i, 1)));
            int[] edge1 = getEdge(0, 2);

            // All pages except white, yellow, the first, and the second page.
            for (int side = 1; side < 5; side++) {
                if (side == combinations.getSide(step, i, 0) || side == combinations.getSide(step, i, 1)) continue;

                for (int edge = 0; edge < 4; edge++) {
                    int[] edge2 = getEdge(side, edge);
                    int[] edge3 = getEdge(side, nextEdgeClockWise(edge));

                    if (edgesCouldBeNeighbours(edge0, edge3) && edgesCouldBeNeighbours(edge1, edge2))
                        combinations.extendCombination(step, i, side, edge);
                }
            }
        }

        step = 2;

        // Fifth side
        for (int i = 0; i < combinations.totalStepCombinations(step); i++) {

            int[] edge0 = getEdge(combinations.getSide(step, i, 2), nextEdgeCounterClockWise(combinations.getEdge(step, i, 2)));
            int[] edge1 = getEdge(0, 3);
            int[] edge5 = getEdge(combinations.getSide(step, i, 0), nextEdgeClockWise(combinations.getEdge(step, i, 0)));

            // All pages except white, yellow, the first, the second, and the third page.
            for (int side = 1; side < 5; side++) {
                if (side == combinations.getSide(step, i, 0) || side == combinations.getSide(step, i, 1) || side == combinations.getSide(step, i, 2))
                    continue;

                for (int edge = 0; edge < 4; edge++) {
                    int[] edge2 = getEdge(side, edge);
                    int[] edge3 = getEdge(side, nextEdgeClockWise(edge));
                    int[] edge4 = getEdge(side, nextEdgeCounterClockWise(edge));

                    if (edgesCouldBeNeighbours(edge0, edge3) && edgesCouldBeNeighbours(edge1, edge2) && edgesCouldBeNeighbours(edge5, edge4))
                        combinations.extendCombination(step, i, side, edge);
                }
            }
        }

        step = 3;

        // Last side
        for (int i = 0; i < combinations.totalStepCombinations(step); i++) {
            for (int edgeOffset = 0; edgeOffset < 4; edgeOffset++) {
                for (int layer = 3, edge = edgeOffset; layer >= 0; layer--, edge++) {

                    if (edge == 4) edge = 0;

                    int[] edge0 = getEdge(combinations.getSide(step, i, layer), nextOppositeEdge(combinations.getEdge(step, i, layer)));
                    int[] edge1 = getEdge(5, edge);

                    if (!edgesCouldBeNeighbours(edge0, edge1)) break;
                    if (layer == 0) {
                        List<int[][]> orientedScheme = orientScheme(combinations.getCombination(step, i), edgeOffset);
                        if (isPossibleFinalCombination(orientedScheme)) {
                            new DebugOutput().printSchemes(orientedScheme, "scheme");
                            guiModel.setColorScheme(orientedScheme);
                            String solvableString = generateSolvableString(orientedScheme);
                            String string = Search.solution(solvableString, 21, 5, false);
                            guiModel.setSolveString(string);
                            guiModel.callObservers("solutionFound");
                            return;
                        }
                    }
                    // TODO Komplett achsensymetrische Seiten ignorieren, da die Rotation hier keinen Unterschied macht
                }
            }
        }
    }

    /**
     * Since it is not possible to detect and exclude axis-symmetric faces with rules
     * that only compare single faces, whole cubies must be assembled and compared.
     * Axis symmetry is also given when colors from opposite sides meet:
     * 0 White > 5 Yellow
     * 1 Green > 4 Blue
     * 2 Red > 3 Orange
     * @return True if this combination is possible, false if not
     */
    private boolean isPossibleFinalCombination(List<int[][]> orientedScheme) {
        // Edge Cubies
        List<int[]> edgeCubies = new ArrayList<>();

        // Relative coordinates of all 12 edge pieces
        // An edge stone is represented by six values
        // Three values per color: side, x, y
        int[] edgeValues = new int[] {
                0, 1, 0, 1, 1, 2,
                1, 1, 0, 5, 1, 0,
                5, 1, 2, 3, 1, 2,
                3, 1, 0, 0, 1, 2,
                0, 0, 1, 4, 2, 1,
                4, 0, 1, 5, 2, 1,
                5, 0, 1, 2, 2, 1,
                2, 0, 1, 0, 2, 1,
                1, 2, 1, 2, 1, 0,
                3, 2, 1, 2, 1, 2,
                1, 0, 1, 4, 1, 0,
                3, 0, 1, 4, 1, 2
        };

        for (int i = 0; i < 72; i += 6) {
            // 
            int color0 = orientedScheme.get(edgeValues[i])[edgeValues[i+1]][edgeValues[i+2]];
            int color1 = orientedScheme.get(edgeValues[i+3])[edgeValues[i+4]][edgeValues[i+5]];
            int[] colors = new int[]{color0, color1};
            Arrays.sort(colors);
            if (isNewCubie(edgeCubies, colors))
                edgeCubies.add(colors);
            else return false;
        }

        // Corner Cubies
        List<int[]> cornerCubies = new ArrayList<>();

        // Relative coordinates of all 8 corner pieces
        // An corner stone is represented by nine values
        // Three values per color: side, x, y
        int[] cornerValues = new int[] {
                0, 0, 0, 1, 0, 2, 4, 2, 0,
                0, 2, 0, 1, 2, 2, 2, 0, 0,
                0, 2, 2, 3, 2, 0, 2, 0, 2,
                0, 0, 2, 3, 0, 0, 4, 2, 2,
                5, 0, 0, 1, 2, 0, 2, 2, 0,
                5, 2, 0, 1, 0, 0, 4, 0, 0,
                5, 2, 2, 3, 0, 2, 4, 0, 2,
                5, 0, 2, 3, 2, 2, 2, 2, 2
        };

        for (int i = 0; i < 72; i += 9) {
            int color0 = orientedScheme.get(cornerValues[i])[cornerValues[i+1]][cornerValues[i+2]];
            int color1 = orientedScheme.get(cornerValues[i+3])[cornerValues[i+4]][cornerValues[i+5]];
            int color2 = orientedScheme.get(cornerValues[i+6])[cornerValues[i+7]][cornerValues[i+8]];
            int[] colors = new int[]{color0, color1, color2};
            Arrays.sort(colors);
            if (isNewCubie(cornerCubies, colors))
                cornerCubies.add(colors);
            else return false;
        }
        return true;
    }

    /**
     * Tests if the cubie already exists in the given list
     * @param cubies A list with all already existing cubies.
     *               The list must always contain either edge or corner cubies.
     * @param colors Color values of a cubie to be checked for uniqueness.
     *               The array must always contain exactly two or three color values
     * @return True if the cubie is unique. False if not.
     */
    private boolean isNewCubie(List<int[]> cubies, int[] colors) {
        for (int[] cubie : cubies) {
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] != cubie[i]) break;
                if (i == colors.length - 1) return false;
            }
        }
        return true;
    }

    /**
     * Generates a string from the given scheme that the solution algorithm can work with.
     * Converts the two dimensional array into a string. Colors are indicated with single
     * letters instead of numbers. The letters stand for the position of the side on
     * which the color is in the middle. For example, white is always represented by an F,
     * because white is always at the front of the scheme.
     *
     * A string consisting of letters only. A side is always represented by nine letters.
     * For example, the first nine colors are on the top side of the scheme.
     * The order is: F -> Front, U -> Up, R -> Right, D -> Down, L -> Left, B -> Back
     * @param scheme The sorted valid scheme.
     * @return A string consisting of letters only.
     */
    private String generateSolvableString(List<int[][]> scheme) {
        // Colors corresponding to their position
        String[] ref = new String[] {"F", "U", "R", "D", "L", "B"};
        String[] orientations = new String[6];

        for (int i = 0; i < 6; i++)
            orientations[scheme.get(i)[1][1]] = ref[i];

        // The order of the sides, which the solution algorithm needs is
        // 1, 2, 0, 3, 4, 5
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i < 6; i++) {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    stringBuilder.append(orientations[scheme.get(i)[x][y]]);
                }
            }
            if (i == 2) i = -1;
            else if (i == 0) i = 2;
        }
        return stringBuilder.toString();
    }

    /**
     * Applies the calculated combination to the schema.
     * @param finalCombination The valid calculated combination.
     * @param lastLayerRotation The rotation of the last layer (yellow).
     * @return The oriented scheme, that represents the scanned cube.
     */
    private List<int[][]> orientScheme(List<int[]> finalCombination, int lastLayerRotation) {
        List<int[][]> scheme = new ArrayList<>();

        // Add the first side (always white)
        scheme.add(sortedScheme.get(0));

        // Add all sides between the white and yellow side
        for (int i = 0; i < 4; i++)
            scheme.add(rotateSide(sortedScheme.get(finalCombination.get(i)[0]), finalCombination.get(i)[1], i));

        // Add the last side (always yellow)
        scheme.add(rotateSide(sortedScheme.get(5), lastLayerRotation, 7));

        return scheme;
    }

    /**
     * Rotates one side clockwise by the specified number of 90° steps. Since three
     * steps of 90° is the same as a rotation of -90°, instead of rotating three
     * times 90°, a rotation of 90° is done counterclockwise. The same is the case
     * for a 180° rotation.
     * @param input The side to be rotated.
     * @param steps How many steps the side should be turned clockwise.
     * @param side The index of the side.
     * @return The rotated side.
     */
    private int[][] rotateSide(int[][] input, int steps, int side) {

        // Since the steps only tell which edge is facing the white side,
        // the steps must be converted depending on their position
        if (steps == 1) steps = 3;
        else if (steps == 3) steps = 1;
        steps = (steps + 2 + side) % 4;

        switch (steps) {
            case 0:
                return input;
            case 1:
                return rotateSideClockwise(input);
            case 2:
                return rotateSide180(input);
            case 3:
                return rotateSideCounterclockwise(input);

        }
        return null;
    }

    /**
     * Rotates the given side by 90°.
     * @param input The side to be rotated.
     * @return The rotated side.
     */
    private int[][] rotateSideClockwise(int[][] input) {
        int[][] output = new int[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                output[2 - y][x] = input[x][y];
            }
        }
        return output;
    }

    /**
     * Rotates the given side by -90°.
     * @param input The side to be rotated.
     * @return The rotated side.
     */
    private int[][] rotateSideCounterclockwise(int[][] input) {
        int[][] output = new int[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                output[y][2 - x] = input[x][y];
            }
        }
        return output;
    }

    /**
     * Rotates the given side by 180°.
     * @param input The side to be rotated.
     * @return The rotated side.
     */
    private int[][] rotateSide180(int[][] input) {
        int[][] output = new int[3][3];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                output[2 - x][2 - y] = input[x][y];
            }
        }
        return output;
    }

    /**
     * Tests if the edges could be neighbours.
     * The rules are:
     *  - No single cubie can have the same color more than once
     *  - No cubie can have two colors that are supposed to be on the
     *    opposite side of the cube (white-yellow, blue-green, red-orange)
     * @param edge0 The first edge, containing two corner and one edge pieces (3x1).
     * @param edge1 The second edge, containing two corner and one edge pieces (3x1).
     * @return True if the given 3x1 edges are possible neighbours.
     */
    private boolean edgesCouldBeNeighbours(int[] edge0, int[] edge1) {
        for (int i = 0; i < 3; i++)
            if (edge0[i] == edge1[2 - i] || edge0[i] + edge1[2 - i] == 5)
                return false;
        return true;
    }

    /**
     * Returns the edge that is located next to the given edge index.
     * @param input The index of the starting edge.
     * @return The index of the next edge counter clock wise.
     */
    private int nextEdgeCounterClockWise(int input) {
        if (input > 0) return input - 1;
        else return 3;
    }

    /**
     * Returns the edge that is located next to the given edge index.
     * @param input The index of the starting edge.
     * @return The index of the next edge clock wise.
     */
    private int nextEdgeClockWise(int input) {
        if (input < 3) return input + 1;
        else return 0;
    }

    /**
     * Returns the edge that is located at the opposite edge.
     * @param input The index of the starting edge.
     * @return The index of the edge at the opposite side.
     */
    private int nextOppositeEdge(int input) {
        if (input <= 1) return input + 2;
        else return input - 2;
    }

    /**
     * Counts whether all of the six colors occur exactly nine times in the given scheme.
     * @param scheme The scheme that should be tested.
     * @return True if all six colors occur exactly nine times. False if not.
     */
    private boolean colorsExistsNineTimes(List<int[][]> scheme) {
        int[] colorCounter = new int[] {0, 0, 0, 0, 0, 0};
        for (int i = 0; i < 6; i++) {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    switch(scheme.get(i)[x][y]) {
                        case 0:
                            colorCounter[0]++;
                            break;
                        case 1:
                            colorCounter[1]++;
                            break;
                        case 2:
                            colorCounter[2]++;
                            break;
                        case 3:
                            colorCounter[3]++;
                            break;
                        case 4:
                            colorCounter[4]++;
                            break;
                        case 5:
                            colorCounter[5]++;
                            break;
                    }
                }
            }
        }
        for (int i = 0; i < 6; i++) {
            if (colorCounter[i] != 9) return false;
        }
        return true;
    }

    /**
     * Returns the 3x1 edge of the given side.
     *
     * @param sideIndex side index between 0 and 5.
     * @param edgeIndex edge index between 0 and 3.
     * @return The edge, consisting of three colors.
     */
    int[] getEdge(int sideIndex, int edgeIndex) {
        switch (edgeIndex) {
            case 0:
                return new int[] {
                        sortedScheme.get(sideIndex)[0][0],
                        sortedScheme.get(sideIndex)[1][0],
                        sortedScheme.get(sideIndex)[2][0]
                };
            case 1:
                return new int[] {
                        sortedScheme.get(sideIndex)[2][0],
                        sortedScheme.get(sideIndex)[2][1],
                        sortedScheme.get(sideIndex)[2][2]
                };
            case 2:
                return new int[] {
                        sortedScheme.get(sideIndex)[2][2],
                        sortedScheme.get(sideIndex)[1][2],
                        sortedScheme.get(sideIndex)[0][2]
                };
            case 3:
                return new int[] {
                        sortedScheme.get(sideIndex)[0][2],
                        sortedScheme.get(sideIndex)[0][1],
                        sortedScheme.get(sideIndex)[0][0]
                };
        }
        return null;
    }

    /**
     * Initial sorting of the schema by color, the following algorithms can determine
     * the color of a side without unnecessary operations. The color of a side is
     * determined by the color of its center stone. The order is determined by the
     * values of the respective colors.
     * @param unsorted The unsorted scheme.
     * @return The sorted scheme.
     */
    private List<int[][]> sortScheme(List<int[][]> unsorted) {
        List<int[][]> sorted = new ArrayList<>();

        // Fill the array with placeholder sides
        for (int i = 0; i < 6; i++)
            sorted.add(new int[3][3]);

        // Set the individual sides to the place of the value of the respective center stone
        for (int[][] singleSide : unsorted) sorted.set(singleSide[1][1], singleSide);
        return sorted;
    }

    private int[] mirrorEdge(int[] input) {
        int[] output = new int[3];
        for (int i = 0; i < 3; i++) {
            output[i] = input[2 - i];
        }
        return output;
    }

    private int[][] mirrorSide(int[][] side) {
        int[][] mirroredSide = new int[3][3];
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (x == 0) mirroredSide[x][y] = side[2][y];
                else if (x == 1) mirroredSide[x][y] = side[x][y];
                else mirroredSide[x][y] = side[0][y];
            }
        }
        return mirroredSide;
    }

    /**
     * Combination system class
     */
    private class Combinations extends ArrayList<ArrayList<ArrayList<int[]>>> {

        Combinations() {
            for (int i = 0; i < 5; i++)
                this.add(new ArrayList<>());
        }

        private List<int[]> getCombination(int step, int index) {
            return this.get(step).get(index);
        }

        private int getSide(int step, int index, int layer) {
            return this.get(step).get(index).get(layer)[0];
        }

        private int getEdge(int step, int index, int layer) {
            return this.get(step).get(index).get(layer)[1];
        }

        private void addNewCombination(int side, int edge) {
            ArrayList<int[]> combination = new ArrayList<>();
            combination.add(new int[]{side, edge});
            this.get(0).add(combination);
        }

        private void extendCombination(int step, int index, int side, int edge) {
            ArrayList<int[]> combination = new ArrayList<>(this.get(step).get(index));
            combination.add(new int[]{side, edge});
            this.get(step + 1).add(combination);
        }

        private int totalStepCombinations(int step) {
            return this.get(step).size();
        }
    }

    public int[][] get(int index) {
        return sortedScheme.get(index);
    }

    @Override
    public void update(Observable o, Object arg) {
        switch ((String)arg) {
            case "cubeFound":
                buildCube();
                break;
        }
    }

    public void initModel(GuiModel guimodel) {
        this.guiModel = guimodel;
    }
}
