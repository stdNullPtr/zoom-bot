package com.xaxoxuxu.application.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SeleniumService
{
    private Callable<Void> taskTemplate;
    private final List<Callable<Void>> currentTasks;
    private final List<ChromeDriver> drivers;
    private final ChromeOptions options;
    private final AtomicInteger runningInstances;
    private ExecutorService service;

    public SeleniumService()
    {
        WebDriverManager.chromedriver().setup();
        taskTemplate = null;
        currentTasks = new ArrayList<>();
        drivers = Collections.synchronizedList(new ArrayList<>());
        options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors", "--disable-extensions", "--no-sandbox", "--disable-dev-shm-usage");
        runningInstances = new AtomicInteger(0);
    }

    @Async
    public void StartSeleniumRoutine(String meetingId, String meetingPassword)
    {
        if (runningInstances.get() > 0)
        {
            log.error("Instances already running: {}", runningInstances.get());
            return;
        }

        List<String> usernames = null;

        try
        {
            Path path = Paths.get("input.txt");
            usernames = Files.readAllLines(path);
        } catch (IOException ex)
        {
            log.error("Can't read file!", ex);
        }

        log.info("Starting selenium async instance...");

        for (int i = 0; i < Objects.requireNonNull(usernames).size(); i++)
        {
            List<String> finalUsernames = usernames;
            int finalI = i;
            taskTemplate = () -> AsyncTask(meetingId, meetingPassword, finalUsernames.get(finalI));
            currentTasks.add(taskTemplate);
        }

        service = Executors.newFixedThreadPool(currentTasks.size());
        try
        {
            List<Future<Void>> futures = service.invokeAll(currentTasks);
            futures.forEach(
                    t ->
                    {
                        try
                        {
                            t.get(1, TimeUnit.MINUTES);
                        } catch (Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (InterruptedException e)
        {
            service.shutdown();
            throw new RuntimeException(e);
        }
    }

    private Void AsyncTask(String meetingId, String meetingPassword, String username)
    {
        ChromeDriver driver = new ChromeDriver(options);
        drivers.add(driver);
        runningInstances.incrementAndGet();

        try
        {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            driver.get(String.format("https://us04web.zoom.us/j/%s#success", meetingId));
            driver.findElement(By.xpath("//*[@id=\"onetrust-accept-btn-handler\"]")).click();
            driver.findElement(By.cssSelector(".mbTuDeF1")).click();
            driver.findElement(By.xpath("//*[text() = 'Join from Your Browser']")).click();

            var nameField = driver.findElement(By.xpath("//*[@id=\"inputname\"]"));
            nameField.click();
            nameField.sendKeys(username);

            var joinBtn = driver.findElement(By.xpath("//*[@id=\"joinBtn\"]"));
            scrollIntoView(driver, joinBtn);
            joinBtn.click();
            var agreeBtn = driver.findElement(By.xpath("//*[@id=\"wc_agree1\"]"));
            scrollIntoView(driver, agreeBtn);
            agreeBtn.click();

            var passcodeField = driver.findElement(By.xpath("//*[@id=\"inputpasscode\"]"));
            passcodeField.click();
            passcodeField.sendKeys(meetingPassword);

            driver.findElement(By.xpath("//*[@id=\"joinBtn\"]")).click();

            Thread.sleep(10000);
        } catch (Exception e)
        {
            driver.quit();
            throw new RuntimeException(e);
        }

        return null;
    }

    private static void scrollIntoView(ChromeDriver driver, WebElement el)
    {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", el);
        try
        {
            Thread.sleep(1000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void StopSeleniumRoutine()
    {
        if (runningInstances.get() <= 0)
            return;

        drivers.forEach(ChromiumDriver::quit);
        drivers.clear();

        service.shutdown();
        currentTasks.clear();

        runningInstances.set(0);
    }
}
