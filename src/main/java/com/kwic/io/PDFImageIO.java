package com.kwic.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.ImageIOUtil;
import org.apache.pdfbox.util.PDFImageWriter;


public class PDFImageIO extends PDFImageWriter {
    
    public boolean writeImage(PDDocument document, String imageFormat, String password, int pageNo,String outputPrefix, int imageType, int resolution) throws IOException{
    	
        boolean bSuccess = true;
        @SuppressWarnings("unchecked")
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();
        
        PDPage page = pages.get(pageNo-1);
        BufferedImage image = page.convertToImage(imageType, resolution);
        String fileName = outputPrefix + "." + imageFormat;
        bSuccess &= ImageIOUtil.writeImage(image, fileName, resolution);
        
        return bSuccess;
    }

    public boolean writeImage(PDDocument document, String imageFormat, String password, int startPage, int endPage,
            String outputPrefix, int imageType, int resolution) throws IOException
    {
        boolean bSuccess = true;
        @SuppressWarnings("unchecked")
		List<PDPage> pages = document.getDocumentCatalog().getAllPages();
        int pagesSize = pages.size();
        
        for (int i = startPage - 1; i < endPage && i < pagesSize; i++){
            PDPage page = pages.get(i);
            BufferedImage image = page.convertToImage(imageType, resolution);
            String fileName = outputPrefix + "_" +(i + 1) + "." + imageFormat;
            bSuccess &= ImageIOUtil.writeImage(image, fileName, resolution);
        }
        return bSuccess;
    }
}
