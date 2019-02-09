package brm.dump;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import brm.Conf;
import common.NoPointerText;

public class MenuDumper {
	
	public void dumpAllMenu(XSSFWorkbook xls, String splitDir) throws IOException{
		int[] starts,ends;
		Charset2 cs=Charset2.loadVramCharset();
		Sheet sheet=xls.createSheet("MENU");
		int row=0;
		Row r0=sheet.createRow(row++);
		r0.createCell(0).setCellValue("分类");
		r0.createCell(1).setCellValue("偏移值");
		r0.createCell(2).setCellValue("大小");
		r0.createCell(3).setCellValue("日文");
		r0.createCell(4).setCellValue("中文");
		
		starts=new int[]{0x626cc};
		ends=new int[]{0x627ca};
		for(NoPointerText n:dump(starts, ends, Conf.jpdir+Conf.EXE, cs)){
			buildRow(sheet.createRow(row++), "career", n);
		}
		
		starts=new int[]{0x46d34,0x473a0,0x47690};
		ends=new int[]{0x47398,0x4768a,0x4797e};
		for(NoPointerText n:dump(starts, ends, splitDir+"MAIN/010/1.1", cs)){
			buildRow(sheet.createRow(row++), "item", n);
		}
		
		starts=new int[]{0xba900};
		ends=new int[]{0xba9d8};
		for(NoPointerText n:dump(starts, ends, splitDir+"SC01/080/0.4", cs)){
			buildRow(sheet.createRow(row++), "boss", n);
		}
		
		starts=new int[]{0xc47ec,0xc4af8};
		ends=new int[]{0xc4ae0,0xc4b6a};
		for(NoPointerText n:dump(starts, ends, splitDir+"SC03/001/0.4", cs)){
			buildRow(sheet.createRow(row++), "shop", n);
		}
		
		starts=new int[]{0x68148};
		ends=new int[]{0x685a4};
		for(NoPointerText n:dump(starts, ends, splitDir+"SC01/004/0.4", cs)){
			buildRow(sheet.createRow(row++), "castle", n);
		}
		
		starts=new int[]{0x5bccc};
		ends=new int[]{0x5bd8e};
		for(NoPointerText n:dump(starts, ends, splitDir+"MAIN/012/1.1", cs)){
			buildRow(sheet.createRow(row++), "memcard", n);
		}

		starts=new int[]{0x5afd4,0x5b7f4,0x5b870};
		ends=new int[]{0x5b44a,0x5b838,0x5ba00};
		for(NoPointerText n:dump(starts, ends, splitDir+"MAIN/012/1.1", cs)){
			buildRow(sheet.createRow(row++), "pause", n);
		}
		
		starts=new int[]{0x58B44,0x58b98,0x5914c};
		ends=new int[]{0x58B66,0x58bb5,0x59172};
		for(NoPointerText n:dump(starts, ends, splitDir+"MAIN/012/1.1", cs)){
			buildRow(sheet.createRow(row++), "other", n);
		}
		
		starts=new int[]{0x5B6AC};
		ends=new int[]{0x5B6C8};
		for(NoPointerText n:dump(starts, ends, splitDir+"SC03/012/0.4", cs)){
			buildRow(sheet.createRow(row++), "pressure", n);
		}
		
		starts=new int[]{0x5B990};
		ends=new int[]{0x5B99E};
		for(NoPointerText n:dump(starts, ends, splitDir+"SC03/012/0.4", cs)){
			buildRow(sheet.createRow(row++), "timer", n);
		}
	}
	
	private List<NoPointerText> dump(int[] starts, int[] ends, String file, Charset2 cs) throws IOException{
		List<NoPointerText> texts=new ArrayList<>();
		for(int i=0;i<starts.length;i++){
			texts.addAll(new NoPointerTextReader().loopRead(file, starts[i], ends[i], cs));
		}
		for(NoPointerText n: texts){
			n.addr-=starts[0];
		}
		return texts;
	}
	
	private void buildRow(Row r, String type, NoPointerText n){
		r.createCell(0).setCellValue(type);
		r.createCell(1).setCellValue(n.addr);
		r.createCell(2).setCellValue(n.size);
		r.createCell(3).setCellValue(n.text);
		r.createCell(4).setCellValue("");
	}

}
