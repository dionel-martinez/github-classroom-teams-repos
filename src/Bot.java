
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author dionel.martinez@upr.edu
 * @date August, 2022
 *
 */
public class Bot {

	
	static final String project = "pa0"; 						/* Current Project! */
	static final String organization = "UPRM-CIIC4010-S23/"; 	/* Current Organization! */
	
	
	static WebDriver driver;
	static final String github = "https://github.com/";
	static final String login = github + "login";
	static final String reposSearchQuery = github + "orgs/" + organization + "repositories?q=" + project;

	public static void main(String[] args) {

		// System Property for Chrome Driver
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
		// driver.manage().window().maximize();

		// Set your login info as System Env Vars (github_user, github_pwd) or modify
		// the following.
		final String githubUser = System.getenv("github_user");
		final String githubPwd = System.getenv("github_pwd");

		// Login
		driver.get(login);
		WebElement user = driver.findElement(By.id("login_field"));
		user.sendKeys(githubUser);
		WebElement pwd = driver.findElement(By.id("password"));
		pwd.sendKeys(githubPwd);
		WebElement signInButton = driver.findElement(By.name("commit"));
		signInButton.click();

		// ? Breakpoint here if you have two-factor-auth enabled

		List<String> repoList = new ArrayList<>();

		// Go to current organization repos
		driver.get(reposSearchQuery);
		wt();
		List<WebElement> repos = driver.findElements(By.className("Box-row"));

		for (WebElement element : repos)
			repoList.add(element.findElement(By.className("d-inline-block")).getText());

		WebElement nextButton = driver.findElement(By.className("next_page"));

		while (true) {
			nextButton.click();
			try {

				Thread.sleep(3000);
			} catch (InterruptedException e) {
				System.out.println("Didn't sleep well :(");
			}

			repos = driver.findElements(By.className("Box-row"));

			boolean dup = false;
			for (WebElement element : repos) {
				String repoName = element.findElement(By.className("d-inline-block")).getText();
				if (repoList.contains(repoName)) {
					dup = true;
					break;
				}
				repoList.add(repoName);
			}
			if (dup)
				break;
			nextButton = driver.findElement(By.className("next_page"));
			
		}

		File data = new File("data/repos.csv");
		try {

			BufferedWriter writer = new BufferedWriter(new FileWriter(data));
			writer.write("Team,");
			writer.write("Repository,");
			writer.write("TA,");
			writer.write("Student A,");
			writer.write("Student B,");
			writer.write("Comments");
			writer.newLine();
			for (String repo : repoList) {
				writer.write(repo.substring(repo.indexOf(project) + project.length() + 1) + ',');
				writer.write(github + organization + repo);
				writer.newLine();
			}

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		driver.close();

	}

	public static void wt() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
	}

}
