package de.latlon.ets.core.util;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XdmValue;

/**
 * Provides various utility methods for accessing or manipulating XML
 * representations.
 */
public final class XMLUtils {

    private static final Logger LOGR = Logger.getLogger(XMLUtils.class.getPackage().getName());

    private XMLUtils() {
    }

    /**
     * Writes the content of a DOM node to a String. An XML declaration is
     * omitted.
     * 
     * @param node
     *            The node to be serialized.
     * @return A String representing the content of the given node.
     */
    public static String writeNodeToString(Node node) {
        if (null == node) {
            throw new IllegalArgumentException("Supplied node is null.");
        }
        StringWriter writer = new StringWriter();
        try {
            Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
            Properties outProps = new Properties();
            outProps.setProperty(OutputKeys.ENCODING, "UTF-8");
            outProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            outProps.setProperty(OutputKeys.INDENT, "yes");
            idTransformer.setOutputProperties(outProps);
            idTransformer.transform(new DOMSource(node), new StreamResult(writer));
        } catch (TransformerException ex) {
            LOGR.log(Level.WARNING, "Failed to serialize DOM node: " + node.getNodeName(), ex);
        }
        return writer.toString();
    }

    /**
     * Writes the content of a DOM node to a byte stream. An XML declaration is
     * omitted.
     * 
     * @param node
     *            The node to be serialized.
     * @param outputStream
     *            The destination OutputStream reference.
     */
    public static void writeNode(Node node, OutputStream outputStream) {
        if (null == node) {
            throw new IllegalArgumentException("Supplied node is null.");
        }
        try {
            Transformer idTransformer = TransformerFactory.newInstance().newTransformer();
            Properties outProps = new Properties();
            outProps.setProperty(OutputKeys.METHOD, "xml");
            outProps.setProperty(OutputKeys.ENCODING, "UTF-8");
            outProps.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            outProps.setProperty(OutputKeys.INDENT, "yes");
            idTransformer.setOutputProperties(outProps);
            idTransformer.transform(new DOMSource(node), new StreamResult(outputStream));
        } catch (TransformerException ex) {
            String nodeName = (node.getNodeType() == Node.DOCUMENT_NODE)
                    ? Document.class.cast(node).getDocumentElement().getNodeName() : node.getNodeName();
            LOGR.log(Level.WARNING, "Failed to serialize DOM node: " + nodeName, ex);
        }
    }

    public static String transformToString(Source source) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            return result.getWriter().toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Evaluates an XPath 2.0 expression using the Saxon s9api API.
     * 
     * @param xmlSource
     *            The XML Source.
     * @param expr
     *            The XPath expression to be evaluated.
     * @param nsBindings
     *            A collection of namespace bindings required to evaluate the
     *            XPath expression, where each entry maps a namespace URI (key)
     *            to a prefix (value); if the prefix is an empty string, it
     *            declares the default namespace.
     * @return An XdmValue object representing a value in the XDM data model;
     *         this is a sequence of zero or more items, where each item is
     *         either an atomic value or a node.
     * @throws SaxonApiException
     *             If an error occurs while evaluating the expression; this
     *             always wraps some other underlying exception.
     */
    public static XdmValue evaluateXPath2(Source xmlSource, String expr, Map<String, String> nsBindings)
            throws SaxonApiException {
        Processor proc = new Processor(false);
        XPathCompiler compiler = proc.newXPathCompiler();
        if (null != nsBindings) {
            for (String nsURI : nsBindings.keySet()) {
                compiler.declareNamespace(nsBindings.get(nsURI), nsURI);
            }
        }
        XPathSelector xpath = compiler.compile(expr).load();
        DocumentBuilder builder = proc.newDocumentBuilder();
        XdmNode node = null;
        if (DOMSource.class.isInstance(xmlSource)) {
            DOMSource domSource = (DOMSource) xmlSource;
            node = builder.wrap(domSource.getNode());
        } else {
            node = builder.build(xmlSource);
        }
        xpath.setContextItem(node);
        return xpath.evaluate();
    }

}