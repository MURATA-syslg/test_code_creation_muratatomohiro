package jp.co.sss.lms.ct.f04_attendance;

import static jp.co.sss.lms.ct.util.WebDriverUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;

import jp.co.sss.lms.ct.util.WebDriverUtils;

/**
 * 結合テスト 勤怠管理機能
 * ケース10
 * @author holy
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("ケース10 受講生 勤怠登録 正常系")
public class Case10 {

	/** 前処理 */
	@BeforeAll
	static void before() {
		createDriver();
	}

	/** 後処理 */
	@AfterAll
	static void after() {
		closeDriver();
	}

	@Test
	@Order(1)
	@DisplayName("テスト01 トップページURLでアクセス")
	void test01() {
		goTo("http://localhost:8080/lms/");

		getEvidence(new Object() {
		}, "ケース10_No.01");

		assertEquals(webDriver.getTitle(), "ログイン | LMS");
		assertEquals(webDriver.findElement(By.className("btn")).getAttribute("value"), "ログイン");
		assertEquals(webDriver.getCurrentUrl(), "http://localhost:8080/lms/");
	}

	@Test
	@Order(2)
	@DisplayName("テスト02 初回ログイン済みの受講生ユーザーでログイン")
	void test02() {
		// ログインに必要な要素の取得
		WebElement userID = WebDriverUtils.webDriver.findElement(By.id("loginId"));
		WebElement password = WebDriverUtils.webDriver.findElement(By.id("password"));
		WebElement loginButton = WebDriverUtils.webDriver.findElement(By.cssSelector("[value='ログイン']"));
		userID.clear();
		userID.sendKeys("StudentAA01");
		password.clear();
		password.sendKeys("Password01");

		getEvidence(new Object() {
		}, "ケース10_No.02_1");

		loginButton.click();

		visibilityTimeout(By.className("active"), 10);

		assertEquals(webDriver.getTitle(), "コース詳細 | LMS");

		getEvidence(new Object() {
		}, "ケース10_No.02_2");
	}

	@Test
	@Order(3)
	@DisplayName("テスト03 上部メニューの「勤怠」リンクから勤怠管理画面に遷移")
	void test03() throws InterruptedException {
		WebElement attendanceLink = webDriver.findElement(By.linkText("勤怠"));

		getEvidence(new Object() {
		}, "ケース10_No.03_1");

		attendanceLink.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		assertEquals(webDriver.getTitle(), "勤怠情報変更｜LMS");
		getEvidence(new Object() {
		}, "ケース10_No.03_2");

	}

	@Test
	@Order(4)
	@DisplayName("テスト04 「出勤」ボタンを押下し出勤時間を登録")
	void test04() {
		WebElement punchInButton = webDriver.findElement(By.xpath("//input[@value='出勤']"));

		// テスト実行時刻をHH:mm形式で取得
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String formattedTime = now.format(formatter);

		getEvidence(new Object() {
		}, "ケース10_No.04_1");

		punchInButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		// 打刻された出勤時間を取得
		WebElement attendanceTable = webDriver.findElement(By.xpath("//tr[@class='info']"));
		WebElement punchInRecord = attendanceTable.findElement(By.xpath("./td[3]"));

		assertEquals(punchInRecord.getText(), formattedTime);
		getEvidence(new Object() {
		}, "ケース10_No.04_2");
	}

	@Test
	@Order(5)
	@DisplayName("テスト05 「退勤」ボタンを押下し退勤時間を登録")
	void test05() {
		WebElement punchOutButton = webDriver.findElement(By.xpath("//input[@value='退勤']"));

		// テスト実行時刻をHH:mm形式で取得
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		String formattedTime = now.format(formatter);

		getEvidence(new Object() {
		}, "ケース10_No.05_1");

		punchOutButton.click();

		try {
			// アラートが出てきた場合はOKをクリック
			webDriver.switchTo().alert().accept();
		} catch (NoAlertPresentException e) {
			// アラートが出ていなければスルー
		}

		visibilityTimeout(By.xpath("//h2[text() = '勤怠管理']"), 10);

		// 打刻された退勤時間を取得
		WebElement attendanceTable = webDriver.findElement(By.xpath("//tr[@class='info']"));
		WebElement punchOutRecord = attendanceTable.findElement(By.xpath("./td[4]"));

		assertEquals(punchOutRecord.getText(), formattedTime);
		getEvidence(new Object() {
		}, "ケース10_No.05_2");
	}

}
