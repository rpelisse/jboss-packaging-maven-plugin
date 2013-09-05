/**
 * 
 */
package org.codehaus.mojo.jboss.packaging.util;


import java.io.File;
import java.io.IOException;

import org.codehaus.mojo.jboss.packaging.ESBMojo;

import junit.framework.TestCase;

/**
 * @author Romain Pelisse - <romain@redhat.com>
 *
 */
public class XmlUtilsTest extends TestCase {

	private String deploymentDescriptorDestName;

	public void setUp() {
		try {
			deploymentDescriptorDestName = File.createTempFile("jboss-esb", ".xml").getAbsolutePath();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public void tearDown() {
		new File(deploymentDescriptorDestName).delete();
	}
	
	private void runTestScenario(String filename) {
		File deploymentDescriptorFile = new File(filename);
		XmlUtils.validateDescriptorWithXSD(XmlUtils.buildDocument(deploymentDescriptorFile, deploymentDescriptorDestName), deploymentDescriptorFile,ESBMojo.XSD_DEFAULT_LOCATION);
		
	}
	
	public void testCaseDescriptorValid() {
		runTestScenario("src/test/resources/jboss-esb.xml");
	}
	
	public void testCaseInvalidDescriptor() {
		try {
			runTestScenario("src/test/resources/jboss-esb-invalid.xml");
		} catch (IllegalArgumentException e) {
			return;
		}
		fail("Invalid descriptor not recognized !");
	}
	
	public void testCaseInvalidXsdFile() {
		try {
			runTestScenario("src/test/resources/jboss-esb-invalid-xsd.xml");
		} catch (IllegalStateException e) {
			return;
		}
		fail("Invalid XSD specified but no exception !");
	}
	
	public void testCaseInvalidContent() {
		try {
			runTestScenario("src/test/resources/jboss-esb-invalid-content.xml");
		} catch (IllegalStateException e) {
			return;
		}
		fail("Invalid content in the descriptor, but no exception !");
	}
	
	
}
