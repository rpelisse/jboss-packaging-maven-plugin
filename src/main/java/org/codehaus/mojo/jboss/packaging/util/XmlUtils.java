/**
 * 
 */
package org.codehaus.mojo.jboss.packaging.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Romain Pelisse - <romain@redhat.com>
 *
 */
public final class XmlUtils {

	private XmlUtils() {}
	
    public static Document buildDocument(File deploymentDescriptorFile, String deploymentDescriptorDestName) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			return dbFactory.newDocumentBuilder().parse(deploymentDescriptorFile);
		} catch (ParserConfigurationException e) {
			throwExceptionForInvalidXmlFile(e, deploymentDescriptorFile);
		} catch (SAXException e) {
			throwExceptionForInvalidXmlFile(e, deploymentDescriptorFile);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}		
 		throw new IllegalArgumentException("Not a valid XML file: " + deploymentDescriptorDestName);
    }
    
	private static void throwExceptionForInvalidXmlFile(Throwable rootCause, File deploymentDescriptorFile) {
		throw new IllegalArgumentException("The provided descriptor is not a valid XML file:" + deploymentDescriptorFile.getAbsolutePath(),rootCause);	
	}

	
	public static Document validateDescriptorWithXSD(Document doc, File deploymentDescriptorFile, String xsdLocation) {
   		validateDescriptor(deploymentDescriptorFile, xsdLocation + File.separator + readXsdVersionFromFile(doc));
		return doc;   	
    }
    
    private static String readXsdVersionFromFile(Document doc) {
    	final String JBOSS_ESB = "jbossesb";
      	NodeList nodes = doc.getChildNodes();    
    	if (nodes != null ) {
    		for ( int i = 0; i < nodes.getLength() ; i++) {
    			Node node = nodes.item(i);    			
    			if ( JBOSS_ESB.equals(node.getNodeName())  ) {
    				NamedNodeMap attributes = node.getAttributes();
    				for ( int j = 0 ; j < attributes.getLength() ; j++) {
    					Node attribute = attributes.item(j);
    					if ( "xmlns".equals(attribute.getNodeName())) {
    						String value = attribute.getNodeValue();
    						if ( value.contains(JBOSS_ESB) && value.endsWith(".xsd"))
    							return value.substring(value.lastIndexOf('/') + 1, value.length());
    						else
    							throw new IllegalStateException("The ESB descriptor points to an invalid XSD" + value);
    					}
    				}
    			}
    		}
    	  	throw new IllegalArgumentException("No root node " + JBOSS_ESB + " found.");
    	} else
    		throw new IllegalArgumentException("Descriptor has no root element !");    	
    }

    private static InputStream loadJbossEsbXsd(String pathToJbossEsbXsd) {
    	InputStream stream = XmlUtils.class.getResourceAsStream(File.separator + pathToJbossEsbXsd);
    	if ( stream == null )
    		throw new IllegalStateException("Can't load XSD " + pathToJbossEsbXsd + " from classpath");
    	return stream;
    }
    
    private static void validateDescriptor(File deploymentDescriptorFile, String jbossEsbXsdVersion) {        
		try {
			SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
					new StreamSource(loadJbossEsbXsd(jbossEsbXsdVersion))).newValidator().
						validate(new StreamSource(deploymentDescriptorFile));
		} catch (SAXException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

    }

}
