package com.kwic.util.pdf;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;

public class ResourceLoaderUserAgent extends ITextUserAgent {

    public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
        super(outputDevice);
    }
    
    protected InputStream resolveAndOpenStream(String uri){
    	InputStream is	= super.resolveAndOpenStream(uri);
    	String fileName	= "";
    	try {
	    	String[] split = uri.split("/");
	    	fileName = split[split.length - 1];
    	} catch (Exception e) {
    		return null;
    	}

    	if (is == null) {
    		// Resource is on the classpath
	    	try{
	    		is = ResourceLoaderUserAgent.class.getResourceAsStream("/project/resources/img/" + fileName);

	    	} catch (Exception e) {
	    	}
    	}

    	if (is == null) {
	    	// Resource is in the file system
	    	try {
	    	is = new FileInputStream(new File("C:\\eclipseFsms\\workspace\\PrintViewer\\WebContent\\img\\" + fileName));
	    	} catch (Exception e) {
	    	}
    	}
    	return is;
   	}
    	
}