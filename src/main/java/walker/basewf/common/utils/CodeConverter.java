package walker.basewf.common.utils;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

/**
 * 文件转码
 */
public class CodeConverter {

	public static void main(String[] args) {

		String srcPath = "D:/workspace/wifi-ftptrans/src";

		String destPath = "D:/workspace/wifi-ftptrans2/src";

		try {
			CodeConverter.convert(srcPath, "GBK", destPath, "UTF-8");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void convert(String srcPath, String srcCode, String destPath, String destCode) throws Exception {

		// 获取所有java文件
		Collection<File> srcFiles = FileUtils.listFiles(new File(srcPath), new String[] { "java" }, true);

		for (File srcFile : srcFiles) {
			// 生成的目录文件
			String destFilename = destPath + srcFile.getCanonicalPath().substring(srcPath.length());

			FileUtils.writeLines(new File(destFilename), destCode, FileUtils.readLines(srcFile, srcCode));
		}
	}
}
