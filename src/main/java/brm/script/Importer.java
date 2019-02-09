package brm.script;

import java.io.IOException;
import java.util.List;

import brm.hack.Encoding;
import brm.hack.Sentence;

public interface Importer {

	List<String> import_(Encoding enc1, List<Sentence> sentences) throws IOException;

}