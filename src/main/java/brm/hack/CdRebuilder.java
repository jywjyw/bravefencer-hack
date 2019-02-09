package brm.hack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;

import brm.CdHeader;
import brm.Conf;
import brm.PacHeader;
import common.Util;

public class CdRebuilder {
	
	public static void main(String[] args) throws IOException {
		rebuild(Conf.desktop+"brmjp", Conf.outdir);
//		for(String cd:Conf.CDS){	//recompressed 0.4 script not equals original
//			System.out.println(Util.md5(new File(String.format("%s%s.CD", Conf.jpdir, cd))));
//			System.out.println(Util.md5(new File(String.format("%s%s.CD", Conf.hackdir, cd))));
//		}
	}
	
	/**
	 * 
	 * @param splitDir
	 * @param rebuildDir generated new *.CD to this dir
	 * @throws IOException
	 */
	public static void rebuild(String splitDir, String rebuildDir) throws IOException{
		ListCdWriter listcd = new ListCdWriter();
		Map<String,Integer> sizes = Conf.getCdSizes();
		byte[] buf=new byte[Conf.LOGIC_BLOCK];
		File[] dirs = new File(splitDir).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		});
		for(File dir : dirs){
			System.out.println("rebuilding "+dir.getName()+".CD....");
			File cdfile=new File(rebuildDir+dir.getName()+".CD");
			Util.mkdirs(cdfile);
			cdfile.delete();
			RandomAccessFile cd = new RandomAccessFile(cdfile, "rw");
			cd.write(buf);	//reserver 0x800 size for cd header, write later...
			CdHeader cdHeader = new CdHeader();
			for(File subcd:dir.listFiles()){
				if(subcd.isDirectory()){
					int linkPacLen=buildLinkPacList(subcd, cd);
					cdHeader.addSubcd((int) linkPacLen);
				} else {
					Util.appendToFileTail(cd, subcd);
					cdHeader.addSubcd((int)subcd.length());		//in cd header, the "length" means valid data length
					cd.write(new byte[Util.get0x800MultipleDiff((int) subcd.length())]);	//but in rom, the valid data must be 0x800 multiple.
				}
			}
			
			cd.seek(0);
			cd.write(cdHeader.build());
			cd.close();
			listcd.append(cdHeader);
			long diff=cdfile.length()-sizes.get(dir.getName());
			if(diff>0) {
				String msg = String.format("%s　is larger than original, exceed %d Bytes", cdfile.getName(), diff);
//				throw new RuntimeException(msg);
				System.out.println("warning!!"+msg);
			}
		}
		System.out.println("rebuilding LIST.CD....");
		listcd.write(rebuildDir+"LIST.CD");
		System.out.println("new *.CD has saved into ["+rebuildDir+"]");
	}
	
	public static int buildLinkPacList(File pacDir, RandomAccessFile cd) throws IOException{
		File[] pacs = pacDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				//sort by file name asc
				return !name.equals("headers.bin");
			}
		});
		RandomAccessFile headersFile = new RandomAccessFile(new File(pacDir,"headers.bin"), "r");
		byte[] buf=new byte[Conf.LOGIC_BLOCK];
		long realLen=0;
		for(int i=0;i<pacs.length;i++){
			File pac = pacs[i];
			if(pac.getName().endsWith(".4")){//recompress file to mem
				BufferedInputStream pacStream = new BufferedInputStream(new FileInputStream(pac));
				byte[] recompress = new Compresser().compress(pacStream);
				pacStream.close();
				headersFile.seek(i*Conf.LOGIC_BLOCK);
				headersFile.read(buf);
				PacHeader pacHeader = PacHeader.parse(buf);
//				System.out.printf("%s: old=%d,new=%d\n",pac,pacHeader.len-PacHeader.SIZE,recompress.length);
				pacHeader.len =recompress.length+PacHeader.SIZE; 
				cd.write(pacHeader.toBytes());
				cd.write(recompress);
				cd.write(new byte[Util.get0x800MultipleDiff((int)recompress.length)]);	//尾部补0对齐到0x800个字节
				realLen+=(Util.get0x800Multiple((int) recompress.length)+PacHeader.SIZE);
			}else{
				headersFile.seek(i*Conf.LOGIC_BLOCK);
				headersFile.read(buf);	
				PacHeader pacHeader = PacHeader.parse(buf);
				pacHeader.len =(int) (pac.length()+PacHeader.SIZE);	//some files are modified, so recalculate file length. 
				cd.write(pacHeader.toBytes());
				Util.appendToFileTail(cd, pacs[i]);
				cd.write(new byte[Util.get0x800MultipleDiff((int) pac.length())]);
				realLen+=(Util.get0x800Multiple((int) pac.length())+PacHeader.SIZE);
			}
		}
		headersFile.close();
		return (int) realLen;
	}
	
	
	
}
