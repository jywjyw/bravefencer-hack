package brm.dump;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import brm.Conf;
import brm.PacHeader;
import common.Util;

public class CdSplitter {
	public static void main(String[] args) throws IOException {
		
		//split Japanese ROM
//		CdSplitter splitter=new CdSplitter(Conf.desktop+"brmjp\\");
//		splitter.split(Conf.jpdir);
		
		//split English ROM
//		CdSplitter splitter=new CdSplitter(Conf.desktop+"brmen\\");
//		splitter.split(Conf.endir);
	}
	
	private String splitDir;
	
	public static CdSplitter newInstance(){
		String splitDir = System.getProperty("java.io.tmpdir");
		if(!splitDir.endsWith(File.separator))
			splitDir+=File.separator;
		splitDir+="brm"+File.separator;
		return new CdSplitter(splitDir);
	}
	
	public CdSplitter(String splitDir) {
		this.splitDir = splitDir;
		File dir=new File(splitDir);
		Util.mkdirs(dir);
		File[] children=dir.listFiles();
		if(children!=null && children.length>0){
			throw new RuntimeException(splitDir+" is not empty!!");
		}
	}
	
	public void split(String cddir) throws IOException {
		long s=System.currentTimeMillis();
		for(String cd:Conf.CDS){
			System.out.println("splitting "+cd+" ....");
			List<File> subcds = new ArrayList<>();
			RandomAccessFile cdfile = new RandomAccessFile(cddir+cd+".CD", "r");
			Map<Integer,Integer> entrance_size = new LinkedHashMap<>();
			int subfilecount = cdfile.readUnsignedByte();
			cdfile.seek(8);
			int index=0;
			for(int i=0;i<subfilecount;i++){
				int entrance = Util.hilo(cdfile.readInt())*Conf.LOGIC_BLOCK;
				int size = Util.hilo(cdfile.readInt());
				entrance_size.put(entrance, size);
			}
			
			new File(splitDir+cd).mkdirs();
			index=0;
			for(Entry<Integer,Integer> e:entrance_size.entrySet()){
				int entrace = e.getKey();
				cdfile.seek(entrace);
				subcds.add(saveSubCd(cdfile, splitDir+cd+File.separator, index, e.getKey(), e.getValue()));
				index++;
			}
			cdfile.close();
			
			//subcd means linkedPacList or sqv or other unknown format
			for(File subcd:subcds){
				RandomAccessFile fis = new RandomAccessFile(subcd,"r");
				byte[] header = new byte[4];
				fis.read(header);
				fis.close();
				if(Arrays.equals(header, PacHeader.MAGIC)){
					splitPac(subcd);
					subcd.delete();
				} else if(Arrays.equals(header, Conf.SQV_MAGIC)){
					String i = subcd.getName().split("-")[0];
					subcd.renameTo(new File(subcd.getParent()+File.separator+i+".sqv"));
				}
			}
		}
		
		System.out.println("split finished. cost(sec):"+(System.currentTimeMillis()-s)/1000);
	}
	
	private File saveSubCd(RandomAccessFile cdfile, String dir, int index, int entrance, int size) throws IOException{
		File subfile = new File(dir+String.format("%03d-%08X", index, entrance));
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(subfile));
		byte[] buf = new byte[size];
		cdfile.read(buf);
		fos.write(buf);
		fos.flush();
		fos.close();
		return subfile;
	}
	
	private void splitPac(File linkedPacList) throws IOException{
		RandomAccessFile linkedPacListStream = new RandomAccessFile(linkedPacList, "r");
		String indexInCd = linkedPacList.getName().split("-")[0];
		File dir = new File(linkedPacList.getParent()+File.separator+indexInCd);
		dir.mkdir();
		boolean endPac=false;
		int pacAddr = 0, index=0;
		BufferedOutputStream oheader= new BufferedOutputStream(new FileOutputStream(new File(dir,"headers.bin")));
		
		while(!endPac){
			linkedPacListStream.seek(pacAddr);
			PacHeader h = PacHeader.read(linkedPacListStream);
			oheader.write(h.toBytes());
			endPac = h.endFlag==1;
			
			byte[] buf = new byte[h.len-Conf.LOGIC_BLOCK];
			linkedPacListStream.read(buf);
			BufferedOutputStream fos=new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath()+File.separator+index+"."+h.pacType));
			if(h.pacType==4){	//4=compressed file
				Uncompresser.uncompress(new ByteArrayInputStream(buf), fos);
			} else {
				fos.write(buf);
			}
			fos.flush();
			fos.close();
			
			h.len=Util.get0x800Multiple(h.len);
			pacAddr += h.len;
			index++;
		}
		linkedPacListStream.close();
		oheader.flush();
		oheader.close();
	}
	
	
	
	/**
	 * 
	 * @param file e.g. MAIN/001/0.1
	 * @return
	 */
	public File getFile(String file){
		return new File(splitDir+file);
	}
	
	public String getSplitDir() {
		return splitDir;
	}

	public void dispose(){
		delete(new File(splitDir));
	}
	
	private void delete(File f){
		if(f.isDirectory()) {
			for(File _f:f.listFiles()){
				delete(_f);
			}
		}
		f.delete();
	}

}
