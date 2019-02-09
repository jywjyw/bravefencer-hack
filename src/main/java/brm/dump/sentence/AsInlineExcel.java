package brm.dump.sentence;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.script.Exporter;

public class AsInlineExcel implements Exporter.Callback{
	
	XSSFWorkbook book ;
	XSSFSheet sheet;
	int rowNum=0;
	Row row;
	StringBuilder sentence = new StringBuilder();
	
	public AsInlineExcel(XSSFWorkbook book, String sheetname) {
		this.book = book;
		this.sheet = book.createSheet(sheetname);
		row = sheet.createRow(rowNum++);
		row.createCell(0).setCellValue("脚本");
		row.createCell(1).setCellValue("起始地址");
		row.createCell(2).setCellValue("长度");
		row.createCell(3).setCellValue("原文");
		row.createCell(4).setCellValue("译文");
	}
	
	@Override
	public void sentenceStart(String file, int sentenceIndex, long sentenceAddr, String english) {
		sentence = new StringBuilder();
	}

	@Override
	public void everyWord(byte[] wordBytes, String word) {
		sentence.append(word);
	}

	@Override
	public void sentenceEnd(String file, int sentenceIndex, long sentenceAddr, int totalLen) {
		row = sheet.createRow(rowNum++);
		row.createCell(0).setCellValue(file);
		row.createCell(1).setCellValue(hex(sentenceAddr));
		row.createCell(2).setCellValue(totalLen);
		row.createCell(3).setCellValue(sentence.toString());
	}
	
	private String hex(long sentenceId){
		return String.format("%05X", sentenceId);
	}
	
}
