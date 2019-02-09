package brm.dump.sentence;

import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.dump.OriginalTexts;
import brm.dump.SentenceSplitter;
import brm.dump.OriginalTexts.OriginalText;

public class Texts2MultilineExcel {
	
	static final boolean LEFT=true,RIGHT=false;
	
	XSSFWorkbook book ;
	XSSFSheet sheet;
	Row row;
	boolean lastDirection=LEFT;
	StringBuilder text = new StringBuilder();
	
	long sentenceId;

	public Texts2MultilineExcel(XSSFWorkbook book) {
		this.book = book;
		this.sheet = book.createSheet("SCRIPTS");
		Row row = sheet.createRow(0);
		int cell=0;
		row.createCell(cell++).setCellValue("语句编号");
		row.createCell(cell++).setCellValue("控制符");
		sheet.setColumnWidth(cell, 255*50);
		row.createCell(cell++).setCellValue("原文");
		sheet.setColumnWidth(cell, 255*50);
		row.createCell(cell++).setCellValue("中文");
		sheet.setColumnWidth(cell, 255*100);
		row.createCell(cell++).setCellValue("英文");
	}
	
	public void exportXls(OriginalTexts texts, Map<String,String> english){
		int i=0;
		for(OriginalText t:texts){
			sentenceStart(i, t.en);
			new SentenceSplitter().splitToWords(t.jp, new SentenceSplitter.Callback() {
				@Override
				public void onReadWord(boolean isCtrl, String word) {
					if(isCtrl && !word.startsWith("[c")) {
						if(lastDirection==RIGHT){
							newRow();
						}
						append(row, 1, word);
						lastDirection = LEFT;
					} else {
						append(row, 2, word);
						lastDirection = RIGHT;
					}
				}
			});
			sentenceEnd(i);
			i++;
		}
	}
	
	private void append(Row row, int cellIndex, String val) {
		Cell cell = row.getCell(cellIndex);
		if(cell==null) 
			cell = row.createCell(cellIndex);
		cell.setCellValue(cell.getStringCellValue()+val);
	}
	
	private void newRow(){
		if(row==null) {
			row = sheet.createRow(1);
		} else {
			row = sheet.createRow(row.getRowNum()+1);
		}
	}
	
	private void sentenceStart(long sentenceId, String english) {
		this.sentenceId=sentenceId;
		newRow();
		row.createCell(0).setCellValue(sentenceId);
		row.createCell(4).setCellValue(english);
	}

	private void sentenceEnd(long sentenceId) {
//		newRow();
	}
}
