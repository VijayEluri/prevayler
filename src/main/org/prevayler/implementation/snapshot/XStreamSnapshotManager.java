//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2004 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//Contributions: Jacob Kjome.

package org.prevayler.implementation.snapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;


/**
 * Writes and reads snapshots to/from XML files.
 *
 * <p>This implementation requires the <a href="http://xstream.codehaus.org/">XStream</a>
 * Java and XML language binding framework which provides for Java object XML serialization.</p>
 *
 * <p>Note that XStream has some dependencies of its own.  It requires the standard XML API's
 * (xml-apis.jar from the <a href="http://xml.apache.org/xerces2-j/">Apache Xerces2-j</a> project or j2sdk1.4+)
 * and an XML implementation (again, provided by Xerces2 or j2sdk1.4+).</p>
 *
 * <p>To make XStream up to 10x faster, add <a href="http://www.extreme.indiana.edu/xgws/xsoap/xpp/mxp1/">XPP3</a>
 * to the classpath. XStream has the concept of a
 * <a href="http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamDriver.html">HierarchicalStreamDriver</a>
 * and the default implementation for XStream is the highly performant XppDriver.  However, XStream will fall back to the DomDriver if XPP3 is
 * not found in the classpath making the XPP3 library entirely optional.</p>
 *
 * @see org.prevayler.implementation.snapshot.SnapshotManager
 */
public class XStreamSnapshotManager extends SnapshotManager {

    private XStream _xstream;

    /**
     * Creates a new XStreamSnapshotManager using a default XStream instance.
     * 
     * <p>This default instance uses the XppDriver for XStream if the XPP3 library is
     * available on the classpath.</p>
     * 
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
     */
	public XStreamSnapshotManager(Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		this(new XStream(), newPrevalentSystem, snapshotDirectoryName);
	}

	/**
	 * Creates a new XStreamSnapshotManager using a pre-configured XStream instance.
	 * 
	 * <p>It is recommended to use the XStream XppDriver (used in XStream by default)
     * to achieve maximum performance</p>
	 *  
	 * @param xstream a pre-configured XStream instance.
     * @param newPrevalentSystem the prevalent system to snapshot.
     * @param snapshotDirectoryName the directory name where the snapshot must be stored.
     * @throws ClassNotFoundException if some class from the system cannot be found. 
     * @throws IOException if there's a problem reading the latest snapshot.
	 */
	public XStreamSnapshotManager(XStream xstream, Object newPrevalentSystem, String snapshotDirectoryName) throws ClassNotFoundException, IOException {
		_xstream = xstream;
        init(newPrevalentSystem, snapshotDirectoryName);
	}

    /**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#writeSnapshot(Object, OutputStream)
	 */
	public void writeSnapshot(Object prevalentSystem, OutputStream out) throws IOException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(out));
		try {
			_xstream.toXML(prevalentSystem, writer);
		} catch (StreamException se) {
			throw new IOException("Unable to serialize with XStream: " + se.getMessage());
		} finally {
			if (writer != null) writer.close();
		}
	}


	/**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#readSnapshot(InputStream)
	 */
	public Object readSnapshot(InputStream in) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(in));
		try {
			return _xstream.fromXML(reader);
		} catch (StreamException se) {
			throw new IOException("Unable to deserialize with XStream: " + se.getMessage());
		} finally {
			if (reader != null) reader.close();
		}
	}


	/**
	 * @see org.prevayler.implementation.snapshot.SnapshotManager#suffix()
	 */
	protected String suffix() {
		return "xstreamsnapshot";
	}

}