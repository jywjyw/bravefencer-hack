package brm.script;

import brm.Conf;
import brm.Script;

public interface ScriptAddrGetter{
	public int getStartAddr();
}

class Script2Addr implements ScriptAddrGetter{
	@Override
	public int getStartAddr() {
		return Conf.SCRIPT2_ADDR;
	}
}

class Script3Addr implements ScriptAddrGetter{
	Script script;
	public Script3Addr(Script script) {
		this.script=script;
	}
	@Override
	public int getStartAddr() {
		return (int) (Conf.SCRIPT2_ADDR+script.parent.length-1);
	}
}

