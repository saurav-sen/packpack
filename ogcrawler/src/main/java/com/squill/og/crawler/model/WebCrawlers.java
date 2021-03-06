//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.01.03 at 03:13:24 PM IST 
//


package com.squill.og.crawler.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WebCrawlers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WebCrawlers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WebTracker" type="{http://www.squilla.co.in/WebCrawlers}WebTracker" minOccurs="0"/>
 *         &lt;element name="Properties" type="{http://www.squilla.co.in/WebCrawlers}Properties" minOccurs="0"/>
 *         &lt;element name="WebCrawler" type="{http://www.squilla.co.in/WebCrawlers}WebCrawler" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WebCrawlers", propOrder = {
    "webTracker",
    "properties",
    "webCrawler"
})
@XmlRootElement
public class WebCrawlers {

    @XmlElement(name = "WebTracker")
    protected WebTracker webTracker;
    @XmlElement(name = "Properties")
    protected Properties properties;
    @XmlElement(name = "WebCrawler")
    protected List<WebCrawler> webCrawler;

    /**
     * Gets the value of the webTracker property.
     * 
     * @return
     *     possible object is
     *     {@link WebTracker }
     *     
     */
    public WebTracker getWebTracker() {
        return webTracker;
    }

    /**
     * Sets the value of the webTracker property.
     * 
     * @param value
     *     allowed object is
     *     {@link WebTracker }
     *     
     */
    public void setWebTracker(WebTracker value) {
        this.webTracker = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the webCrawler property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the webCrawler property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWebCrawler().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WebCrawler }
     * 
     * 
     */
    public List<WebCrawler> getWebCrawler() {
        if (webCrawler == null) {
            webCrawler = new ArrayList<WebCrawler>();
        }
        return this.webCrawler;
    }

}
