//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.15 at 04:52:32 PM IST 
//


package com.pack.pack.feed.selection.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.pack.pack.feed.selection.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Strategies_QNAME = new QName("http://www.squilla.co.in/Strategies", "Strategies");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.pack.pack.feed.selection.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Strategies }
     * 
     */
    public Strategies createStrategies() {
        return new Strategies();
    }

    /**
     * Create an instance of {@link Strategy }
     * 
     */
    public Strategy createStrategy() {
        return new Strategy();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Strategies }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.squilla.co.in/Strategies", name = "Strategies")
    public JAXBElement<Strategies> createStrategies(Strategies value) {
        return new JAXBElement<Strategies>(_Strategies_QNAME, Strategies.class, null, value);
    }

}
