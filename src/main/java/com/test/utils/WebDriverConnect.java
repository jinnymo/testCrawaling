package com.test.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

public class WebDriverConnect {

    // 싱글톤 WebDriver 인스턴스를 위한 static 변수
    private static WebDriver driver;

    // 싱글톤 WebDriver 인스턴스를 반환하는 메서드
    public static synchronized WebDriver getWebDriver() {
        if (driver == null) {
            // OS에 따라 WebDriver 경로 설정
            String driverPath = System.getProperty("os.name").toLowerCase().contains("lin")
                    ? "/opt/microsoft/msedge/microsoft-edge"
                    : "src/main/resources/driver/msedgedriver.exe"; // 리눅스의 경우 적절한 경로로 변경
            System.setProperty("webdriver.edge.driver", driverPath);

            // Edge 옵션 설정
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--no-sandbox");
            options.addArguments(
                    "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");

            // EdgeDriver 생성
            driver = new EdgeDriver(options);
        }
        return driver;
    }

    // WebDriver 종료 메서드 (필요한 경우 사용)
    public static synchronized void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
