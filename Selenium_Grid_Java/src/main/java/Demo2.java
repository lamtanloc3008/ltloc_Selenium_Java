import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.net.MalformedURLException;
import java.net.URL;

public class Demo2 {

    public static void Start_Docker(){
        try {

            Execute_Command("docker rm -f $(docker ps -aq)");
            Execute_Command("docker ps");
            Execute_Command("docker compose -f docker-compose-hub-nodes.yml up -d");
            Thread.sleep(10000);
            Execute_Command("docker ps");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void Video_Record (String browserName){
        try {
            if (browserName.equalsIgnoreCase("chrome")) {
                Execute_Command("docker ps");
                Execute_Command("docker compose -f docker-compose-chrome.yml up -d");
                Execute_Command("docker ps");
            } else if (browserName.equalsIgnoreCase("firefox")) {
                Execute_Command("docker ps");
                Execute_Command("docker compose -f docker-compose-firefox.yml up -d");
                Execute_Command("docker ps");
            } else if (browserName.equalsIgnoreCase("edge")) {
                Execute_Command("docker ps");
                Execute_Command("docker compose -f docker-compose-edge.yml up -d");
                Execute_Command("docker ps");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void Stop_Docker(String browserName){
        try {
            if (browserName.equalsIgnoreCase("chrome")) {
                Execute_Command("docker ps");
                Execute_Command("docker stop selenium_grid-chrome_video-1");
                Execute_Command("docker ps");
            } else if (browserName.equalsIgnoreCase("firefox")) {
                Execute_Command("docker ps");
                Execute_Command("docker stop selenium_grid-firefox_video-1");
                Execute_Command("docker ps");
            } else if (browserName.equalsIgnoreCase("edge")) {
                Execute_Command("docker ps");
                Execute_Command("docker stop selenium_grid-edge_video-1");
                Execute_Command("docker ps");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void Remove_All_Docker(){
        try {
            Execute_Command("docker ps");
            Execute_Command("docker rm -f $(docker ps -aq)");
            Execute_Command("docker ps");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void Execute_Command(String command) throws IOException, InterruptedException {

        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        List<String> outputLines = new ArrayList<>();

        while ((line = reader.readLine()) != null) {
            outputLines.add(line);
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Exit Code: " + exitCode);

        if (exitCode != 0) {
            System.err.println("Command execution failed: " + command);
            for (String outputLine : outputLines) {
                System.err.println(outputLine);
            }
        }
    }

    public static void Run_Browser_Test(String browserName, String url, String local_host) throws InterruptedException, MalformedURLException {
        WebDriver driver = null;

        if (browserName.equalsIgnoreCase("chrome")) {
            ChromeOptions cap = new ChromeOptions();
            driver = new RemoteWebDriver(new URL(local_host), cap);
        } else if (browserName.equalsIgnoreCase("firefox")) {
            FirefoxOptions cap = new FirefoxOptions();
            driver = new RemoteWebDriver(new URL(local_host), cap);
        } else if (browserName.equalsIgnoreCase("edge")) {
            EdgeOptions cap = new EdgeOptions();
            driver = new RemoteWebDriver(new URL(local_host), cap);
        }

        Video_Record(browserName);
        Thread.sleep(5000);

        driver.get(url);
        driver.manage().window().maximize();
        Thread.sleep(2000);

        // Checkbox
        WebElement checkboxesLink = driver.findElement(By.linkText("Checkboxes"));
        checkboxesLink.click();
        Thread.sleep(2000);

        WebElement checkbox1 = driver.findElement(By.xpath("//input[@type='checkbox'][1]"));
        checkbox1.click();
        Thread.sleep(2000);

        Assert.assertTrue(checkbox1.isSelected());
        Thread.sleep(5000);
        driver.navigate().back();
        Thread.sleep(2000);

        // Dropdown
        WebElement dropdownLink = driver.findElement(By.linkText("Dropdown"));
        dropdownLink.click();
        Thread.sleep(2000);

        WebElement dropdown = driver.findElement(By.id("dropdown"));
        dropdown.sendKeys("Option 1");
        Thread.sleep(2000);

        Assert.assertEquals(dropdown.getAttribute("value"), "1");
        Thread.sleep(5000);
        driver.navigate().back();
        Thread.sleep(2000);

        // Alerts
        WebElement jsAlertsLink = driver.findElement(By.linkText("JavaScript Alerts"));
        jsAlertsLink.click();
        Thread.sleep(2000);

        WebElement jsAlertButton = driver.findElement(By.xpath("//button[text()='Click for JS Alert']"));
        jsAlertButton.click();
        Thread.sleep(2000);

        driver.switchTo().alert().accept();
        Thread.sleep(2000);

        WebElement result = driver.findElement(By.id("result"));
        Assert.assertTrue(result.getText().equalsIgnoreCase("You successfully clicked an alert"));
        Thread.sleep(5000);

        // Quit
        driver.quit();

//        Stop_Docker(browserName);
    }

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        String url = "https://the-internet.herokuapp.com/";
        String local_host = "http://192.168.81.183:4444";

        Start_Docker();
        Run_Browser_Test("chrome", url, local_host);
//        Run_Browser_Test("firefox", url, local_host);
//        Run_Browser_Test("edge", url, local_host);
//        Remove_All_Docker();

    }
}
