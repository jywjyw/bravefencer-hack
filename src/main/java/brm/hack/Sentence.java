package brm.hack;

public class Sentence {
	public String sentence,script;
	public int len,addr;
	public Sentence(String sentence, String script, int len, int addr) {
		this.sentence = sentence;
		this.script = script;
		this.len = len;
		this.addr = addr;
	}
}
