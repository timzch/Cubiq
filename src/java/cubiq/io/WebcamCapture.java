package cubiq.io;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.List;

public class WebcamCapture {

    private VideoCapture videoCapture;
    private int framerate;

    public WebcamCapture() {
        createWebcamCapture(0);
    }

    public void createWebcamCapture(int webcamIndex) {
        videoCapture = new VideoCapture(webcamIndex);

        if (!videoCapture.isOpened())
            return;

        // Track the framerate
        framerate = calculateFramerate();

        // Set width and height TODO width/ height von webcam auslesen
        videoCapture.set(3, 1920);
        videoCapture.set(4, 1080);
        // Set the framerate
        videoCapture.set(5, framerate);
    }

    private List<Integer> listAvailableDevices() {
        List<Integer> availableDevices = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            videoCapture.open(i);
            if (videoCapture.isOpened()) availableDevices.add(i);
        }
        return (availableDevices);
    }

    private int calculateFramerate() {
        long before, after;
        before = System.nanoTime();
        for (int i = 0; i < 50; i++)
            videoCapture.read(new Mat());
        after = System.nanoTime();

        double nanosPerFrame = (after - before) / 50d;
        double secPerFrame = nanosPerFrame / 1e9d;
        int framerate = (int) Math.round(1 / secPerFrame);

        // Eliminate small calculation errors by summarizing the results to typical values
        if (framerate < 10) return 0;
        if (framerate < 20) return 15;
        if (framerate < 27) return 25;
        if (framerate < 40) return 30;
        if (framerate < 100) return 60;
        return 120;
    }

    public int getFramerate() {
        return this.framerate;
    }

    public VideoCapture getVideoCapture() {
        return this.videoCapture;
    }
}
