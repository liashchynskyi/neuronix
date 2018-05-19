package controllers;


import java.io.File;
import java.nio.ByteBuffer;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import static org.bytedeco.javacpp.opencv_imgcodecs.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;

public class CameraController {
    
    int width = 0;
    double ratio = 1.37;
    
    public CameraController (int width) {
        this.width = width;
    }
    
    public String videoStream () {
        VideoCapture cap = new VideoCapture(0);
        Mat frame = new Mat();
        
        String saved = null;
        
        while (cap.isOpened()) {
            cap.read(frame);
            if (frame.empty()) {
                System.out.println("Empty frame!");
            }
            else
                namedWindow( "Microscope Cam", CV_WINDOW_AUTOSIZE);
            imshow("Microscope Cam", frame);
            int c = cvWaitKey(1000/60);
            if (c == 27) {
                cap.release();
                destroyAllWindows();
            }
            if (c == 13) {
                cap.release();
                destroyAllWindows();
                saved = crop(frame);
            }
        }
        return saved;
    }
    
    public String crop (Mat img) {
        double basewidth = this.width * this.ratio;
        double ratio = basewidth / img.size().width();
        double h = img.size().height() * ratio;
        double w = img.size().width() * ratio;
    
        System.out.println("computed: w " + w);
        System.out.println("computed: h " + h);
    
        resize(img, img, new Size( (int)w, (int)h ));
    
        double delta_w = basewidth - w;
        double delta_h = basewidth - h;
    
        System.out.println("delta: w " + delta_w);
        System.out.println("delta: h " + delta_h);
    
        int top = (int) delta_h / 2;
        int bottom = (int) delta_h - (int)(delta_h / 2);
        int left = (int) delta_w / 2;
        int right = (int) delta_w - (int)(delta_w / 2);
    
    
        Mat resized = new Mat();
        copyMakeBorder(img, resized, top, bottom, left, right, BORDER_CONSTANT, new Scalar(0));
    
        Rect roi = new Rect((int) w / 8, top, this.width, this.width);
    
        Mat cr = new Mat(resized, roi);
        Mat cr1 = new Mat();
        cr.copyTo(cr1);
    
        File dir = new File(System.getProperty("user.dir") + "\\temp");
        if (!dir.exists()) dir.mkdir();
        
        String path = System.getProperty("user.dir") + "\\temp\\microscope_cropped.jpg";
        System.out.println(path);
        
        imwrite(path, cr1);
    
        System.out.println("after: w " + cr1.size().width());
        System.out.println("after: h " + cr1.size().height());
        
        waitKey(0);
        
        return path;
    }
}