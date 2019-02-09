package brm.dump.sentence;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.script.Exporter;

public class AsMultilineExcel implements Exporter.Callback{
	static final boolean LEFT=true,RIGHT=false;
	
	XSSFWorkbook book ;
	XSSFSheet sheet;
	Row row;
	boolean lastDirection=LEFT;
	StringBuilder text = new StringBuilder();
	
	String file,sentenceId;
	
	public AsMultilineExcel(XSSFWorkbook book, String sheetname) {
		this.book = book;
		this.sheet = book.createSheet(sheetname);
		Row row = sheet.createRow(0);
		int cell=0;
		row.createCell(cell++).setCellValue("脚本");
		row.createCell(cell++).setCellValue("起始地址");
		row.createCell(cell++).setCellValue("长度");
		row.createCell(cell++).setCellValue("控制符");
		sheet.setColumnWidth(cell, 255*50);
		row.createCell(cell++).setCellValue("原文");
		sheet.setColumnWidth(cell, 255*50);
		row.createCell(cell++).setCellValue("中文");
		sheet.setColumnWidth(cell, 255*100);
		row.createCell(cell++).setCellValue("英文");
	}

	private void append(Row row, int cellIndex, String val) {
		Cell cell = row.getCell(cellIndex);
		if(cell==null) cell = row.createCell(cellIndex);
		cell.setCellValue(cell.getStringCellValue()+val);
	}
	
	private void newRow(){
		if(row==null) {
			row = sheet.createRow(1);
		} else {
			row = sheet.createRow(row.getRowNum()+1);
		}
	}

	@Override
	public void sentenceStart(String file, int sentenceIndex, long sentenceAddr, String english) {
		this.file=file;
		this.sentenceId=hex(sentenceAddr);
		newRow();
		row.createCell(0).setCellValue(file);
		row.createCell(1).setCellValue(this.sentenceId);
		row.createCell(6).setCellValue(english);
	}


	@Override
	public void everyWord(byte[] wordBytes, String word) {
		if(word.startsWith("[") && !word.startsWith("[c")) {
			if(lastDirection==RIGHT){
				newRow();
			}
			append(row, 3, word);
			lastDirection = LEFT;
		} else {
			append(row, 4, word);
			lastDirection = RIGHT;
		}
	}

	@Override
	public void sentenceEnd(String file, int sentenceIndex, long sentenceAddr, int sentenceLen) {
//		newRow();
		row.createCell(2).setCellValue(sentenceLen);
	}
	
	private String hex(long sentenceId){
		return String.format("%05X", sentenceId);
	}
}
