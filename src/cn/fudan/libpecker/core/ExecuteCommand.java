package cn.fudan.libpecker.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecuteCommand {

	public static void doBat(String batPath) throws IOException {

		Process p;
		String cmd = batPath;
		cmd = cmd.replaceAll(" ", "\" \"");// 路径中有空格会报�?
		System.out.println("cmd:" + cmd);
		System.out.println("�?��执行cmd");

		p = Runtime.getRuntime().exec("cmd.exe /c " + cmd); // java调用bat文件在windows下相当于直接调用   /�?��/搜索程序和文�? 的指�?
		InputStream fis = p.getInputStream();
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		// 输出执行内容
		while ((line = br.readLine()) != null) {
			System.out.print(line + "\n");
		}

		p.destroy();

	}
}
