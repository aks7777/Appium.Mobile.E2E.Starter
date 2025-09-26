package company.utils;

import company.base.BasePage;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.appium.java_client.ios.IOSStartScreenRecordingOptions;
import io.appium.java_client.screenrecording.CanRecordScreen;
import org.apache.commons.codec.binary.Base64;
import org.testng.ITestResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Date;

import static company.base.BasePage.getDriver;

/**
 * RecordVideo class provides methods for recording the screen during test execution.
 * It includes methods for starting and stopping the recording, and saving the recorded video.
 */
public class RecordVideo {

    private static final boolean ENABLE_MINIO = Boolean.parseBoolean(ReadProperties.getValue("enableMinio"));

    /**
     * This method is used to start the screen recording.
     */
    public void startRecording() {
        if(BasePage.getPlatform().equalsIgnoreCase("android")){
            ((CanRecordScreen) getDriver()).startRecordingScreen(
                    new AndroidStartScreenRecordingOptions()
                            .withBitRate(1600000)
                            .withTimeLimit(Duration.ofMinutes(10)));
        }
        else{
            ((CanRecordScreen) getDriver()).startRecordingScreen(
                    new IOSStartScreenRecordingOptions()
                            .withTimeLimit(Duration.ofMinutes(10)));
        }
    }

    /**
     * This method is used to stop the screen recording and save the recorded video.
     * @param result This is the result of the test execution.
     * @param path This is the path where the recorded video will be saved.
     * @throws IOException If an input or output exception occurred
     */
    public void stopRecording(ITestResult result, String path) throws IOException, InterruptedException {
//        BasePage.wait(3);
        String media = ((CanRecordScreen) getDriver()).stopRecordingScreen();
        File videoDir = new File(path);
        synchronized (videoDir) {
            if (!videoDir.exists()) {
                videoDir.mkdirs();
            }
        }
        FileOutputStream fileOutputStream = null;
        try {
            String videoFilePath = videoDir + File.separator + result.getName() + ".mp4";
            //start timer
            LoggerUtilities.info("Writing to media file is started.");
            LocalTime startTime0 = LocalTime.now();
            fileOutputStream = new FileOutputStream(videoFilePath);
            fileOutputStream.write(Base64.decodeBase64(media));
            LoggerUtilities.info("Recorded path : " + videoFilePath);
            fileOutputStream.close();
            //stop timer
            LocalTime endTime0 = LocalTime.now();
            LoggerUtilities.info("Writing to media file is completed.");

            getTimeDifference(startTime0, endTime0, "Write ");

            // convert video to mp4 format
            String convertedVideoPath = videoDir + File.separator + result.getName() + "_converted.mp4";
            convertVideo(videoFilePath, convertedVideoPath);

            String fileUrl;

            if (ENABLE_MINIO && BasePage.getTestExecutionLocation().equalsIgnoreCase("runner")) {
                // Generate unique folder name
                String folderName = generateUniqueFolderName();

                // File object
                File videoFile = new File(convertedVideoPath);

                // MinioUploader object creation
                MinioUploader uploader = new MinioUploader();

                //start timer
                LocalTime startTime = LocalTime.now();
                LoggerUtilities.info("Uploading to minio is started.");
                // File upload to Minio
                fileUrl = uploader.uploadFile(folderName, videoFile);

                //stop timer
                LocalTime endTime = LocalTime.now();
                LoggerUtilities.info("Uploading to minio is completed.");
                getTimeDifference(startTime, endTime, "Upload ");

                if (fileUrl != null) {
                    LoggerUtilities.info("Uploaded video to Minio");
                } else {
                    LoggerUtilities.error("File upload failed...");
                    fileUrl = videoFilePath; // Fall back to local file path
                }
            } else {
                // Use local file path if Minio upload is disabled
                LoggerUtilities.info("Minio upload is disabled, using local video path.");
                fileUrl = "/" + videoFilePath;
            }

            // Attach video to report
            String logText = "<center><video width=\"320\" height=\"240\" controls>\n" +
                    "  <source src=\"" + fileUrl + "\" type=\"video/mp4\">\n" +
                    "</video><br/><a href=\"" + fileUrl + "\" target=\"_blank\">Download Video</a></center>";
            LoggerUtilities.attachAsInfo(logText);

        } catch (Exception e) {
            LoggerUtilities.error("Error in video recording : " + e.toString());
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }

    /**
     * This method is used to generate a unique folder name for storing the recorded video.
     * @return String This returns the generated folder name.
     */
    public String generateUniqueFolderName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "upload_" + sdf.format(new Date());
    }

    public void getTimeDifference(LocalTime startTime, LocalTime endTime, String msg) throws InterruptedException {
        // Calculate the difference between the two times
        Duration duration = Duration.between(startTime, endTime);

        // Get the seconds and nanoseconds from the duration
        long seconds = duration.getSeconds();
        long millis = duration.toMillis() % 1000;

        // Display the time difference
        LoggerUtilities.info(msg + ", Time difference : " + seconds + " seconds and " + millis + " milliseconds.");
    }

    /**
     * This method is used to convert the video format using ffmpeg.
     * @param inputFilePath The path of the input video file.
     * @param outputFilePath The path where the converted video will be saved.
     */
    public void convertVideo(String inputFilePath, String outputFilePath) {
        try {
            Process process = Runtime.getRuntime().exec(
                    "ffmpeg -i " + inputFilePath + " -c:v libx264 -preset fast -movflags +faststart " + outputFilePath
            );
            process.waitFor();
            LoggerUtilities.info("Video conversion completed. Converted video path: " + outputFilePath);
        } catch (IOException | InterruptedException e) {
            LoggerUtilities.error("Error during video conversion: " + e.toString());
        }
    }

}
