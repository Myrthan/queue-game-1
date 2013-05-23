package queue_game.model;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

/**
 * Class containing actions players that may happen when playing, with additional information
 * 
 * @author Szymon
 */
public class GameAction {
	private GameActionType type;
	private Object[] info;

	private GameAction() {
	}

	public GameAction(GameActionType type, Object... info) {
		this.type = type;
		this.info = Arrays.copyOf(info, info.length);
	}

	/**
	 * Writes current object to the specified Writer.
	 * 
	 * @param out The stream to write to
	 * @throws IOException when something goes wrong
	 */
	public void write(Writer out) throws IOException {
		out.write(type.toString());
		for(Object o: info) {
			if(o instanceof String) {
				out.write(Integer.toString(((String) o).length()));
				out.write(" ");
				out.write((String) o);
				out.write(" ");
			} else {
				out.write(o.toString());
				out.write(" ");
			}
		}
		out.write("\n");
		out.flush();
	}

	private static int readInt(Reader in) throws IOException {
		int i = in.read();
		if(i == -1)
			throw new EOFException("Unexpected end of stream");
		return i;
	}

	/**
	 * Reads an object from the specified Reader.
	 * This function makes almost no attempt to verify whether the object is correct.
	 * 
	 * @param in the stream to read from
	 * @return the read GameAction object
	 * @throws IOException when something goes wrong
	 * @throws EOFException when the stream ends unexpectedly
	 */
	public static GameAction read(Reader in) throws IOException {
		//temporarily disabled
		return new GameAction();
	}

	@Override
	public String toString() {
		return type + " " + Arrays.asList(info);
	}

	/**
	 * @return
	 */
	public GameActionType getType() {
		return type;
	}

	/**
	 * @return
	 */
	public Object[] getInfo() {
		return info;
	}
}
