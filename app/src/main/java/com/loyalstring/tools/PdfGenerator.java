package com.loyalstring.tools;

import static com.loyalstring.Activities.BillViewactivity.decimalFormat;
import static com.loyalstring.Adapters.BillListAdaptor.convertTimestampToDate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PdfGenerator {

    private Context context;
    File downloadsDir;

    public PdfGenerator(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void generatePdf(HashMap<String, List<Itemmodel>> billmap, List<Itemmodel> item, int i) throws IOException {
        // Check for WRITE_EXTERNAL_STORAGE permission

        Log.e("checking formate ", "  "+i);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            if (i == 15) {
                //modern
                savePdfToDownloadFolder(billmap);
            } else if (i == 2) {
                Collections.sort(item, new Comparator<Itemmodel>() {
                    @Override
                    public int compare(Itemmodel item1, Itemmodel item2) {
                        return item1.getProduct().compareToIgnoreCase(item2.getProduct());
                    }
                });
                //reevazz
                savePdfToDownloadFolder1(item);
            } else if (i == 3) {
                //geesee
//                savePdfToDownloadFolder2(item);

                new Thread(() -> {
                    try {
                        savePdfToDownloadFolder2(item); // Move your PDF logic here
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();

            } else if (i == 12) {
                //jjj
                savePdfToDownloadFolder3(item);
            } else if (i == 5) {
//                List<Itemmodel> itemList = getDummyItems();
                savemvspdf(item);
//                savemvspdf(itemList);
            }
//            else if(i == 12){
//                jjjnewpdf(item);
//            }
            else if (i == 13) {
                //vakpati
//                List<Itemmodel> itemList = getDummyItems();
                savePdfToDownloadFolder7(item);
            } else if (i == 20) {
                List<Itemmodel> itemList = getDummyItems();
                pushpa11(billmap, 20);
//                if(f == 1){
//                    //default
//                    List<Itemmodel> itemList = getDummyItems();
//                    pushpa1(billmap);
//                }else if(f == 2){
//
//                }else if(f == 3){
//
//                }else if(f == 4){
//
//                }else if(f == 5){
//
//                }else{
//
//                }
            } else if (i == 37) {
                List<Itemmodel> itemList = getDummyItems();
                Dnj(billmap);

                //  savePdfToDownloadFolder2(item);
            } else if (i == 43) {
                List<Itemmodel> itemList = getDummyItems();
                pushpa11(billmap, 43);

            } else if (i == 44) {
                List<Itemmodel> itemList = getDummyItems();
                pushpa11(billmap, 44);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("Range")
    private void Dnj(HashMap<String, List<Itemmodel>> billmap) {
        String invoiceNumber = "";
        long tdate = 0;
        String cname = "", branch = "", via = "", kt = "", screw = "", tags = "", wast = "";

        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Itemmodel model = entry.getValue().get(0);
                invoiceNumber = model.getInvoiceNumber();
                tdate = model.getOperationTime();
                cname = model.getCustomerName();
                branch = model.getBranch();
                via = model.getDiamondCertificate();
                kt = model.getStockKeepingUnit();
                screw = model.getDiamondColor();
                tags = model.getDiamondMetal();
                wast = String.valueOf(model.getFixedWastage());
                break;
            }
        }

        if (invoiceNumber.isEmpty()) invoiceNumber = "unknown_invoice";

        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, invoiceNumber + ".pdf");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            Uri fileUri = resolver.insert(collection, contentValues);

            if (fileUri == null) {
                Toast.makeText(context, "Failed to create file", Toast.LENGTH_LONG).show();
                return;
            }

            OutputStream outputStream = resolver.openOutputStream(fileUri);
            if (outputStream == null) {
                Toast.makeText(context, "Failed to open output stream", Toast.LENGTH_LONG).show();
                return;
            }

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4.rotate());

            String formattedDate = convertTimestampToDate(tdate);
            document.add(new Paragraph("Proforma Invoice").setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1})).setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER)
                    .add(new Paragraph("DATE: " + formattedDate).setBold())
                    .add(new Paragraph("CLIENT NAME: " + cname).setBold())
                    .add(new Paragraph("NAME & PHONE NO: ").setBold()));
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT)
                    .add(new Paragraph("KT: " + kt).setBold())
                    .add(new Paragraph("SCREW: " + screw).setBold())
                    .add(new Paragraph("SEPARATE TAGS: " + tags).setBold())
                    .add(new Paragraph("WASTAGE: " + wast).setBold())
                    .add(new Paragraph("DELIVERY DATE").setBold()));
            document.add(headerTable);

            float[] columnWidths = {0.5f, 1.0f, 1.5f, 1f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Table dataTable = new Table(UnitValue.createPercentArray(columnWidths)).setWidth(UnitValue.createPercentValue(100));

            String[] headers = {"SNO", "TAG NO", "ITEM NAME", "DESIGN", "STAMP", "G WT", "S WT", "N WT", "FINE", "STN VALUE", "IMAGE"};
            for (String h : headers) dataTable.addHeaderCell(new Cell().add(new Paragraph(h).setBold()));

            int i = 0;
            double totalGrWt = 0.0, totalStWt = 0.0, totalNetWt = 0.0, totalFine = 0.0, totalStnValue = 0.0;

            for (String it : billmap.keySet()) {
                List<Itemmodel> items = billmap.get(it);
                for (Itemmodel item : items) {
                    i++;
                    double grwt = item.getGrossWt();
                    double netwt = item.getNetWt();
                    double stwt = grwt - netwt;
                    double fine1 = (item.getMakingPer() + item.getFixedWastage()) * (netwt / 100);
                    double stnValue = item.getStoneAmount();

                    dataTable.addCell(String.valueOf(i));
                    dataTable.addCell(item.getItemCode());
                    dataTable.addCell(item.getProduct());
                    dataTable.addCell(item.getDiamondClarity());
                    dataTable.addCell(item.getStockKeepingUnit() != null ? item.getStockKeepingUnit() : "");
                    dataTable.addCell(String.format("%.3f", grwt));
                    dataTable.addCell(String.format("%.3f", stwt));
                    dataTable.addCell(String.format("%.3f", netwt));
                    dataTable.addCell(String.format("%.3f", fine1));
                    dataTable.addCell(String.format("%.3f", stnValue));

                    // ==== IMAGE CELL (ALWAYS ADDED) ====
                    String imageUrl = item.getItemCode() + ".jpg";
                    File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files/images/" + imageUrl);

                    if (imageFile.exists()) {
                        ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                        Image image = new Image(imageData);
                        image.setWidth(60).setHeight(60); // Resize image to fit cell
                        dataTable.addCell(new Cell().add(image));
                    } else {
                        dataTable.addCell(new Cell().add(new Paragraph("No Image").setFontSize(8).setItalic()));
                    }

                    // Update totals
                    totalGrWt += grwt;
                    totalStWt += stwt;
                    totalNetWt += netwt;
                    totalFine += fine1;
                    totalStnValue += stnValue;
                }
            }

            // Totals Row
            dataTable.addCell(new Cell(1, 5).add(new Paragraph("TOTAL").setBold()).setTextAlignment(TextAlignment.RIGHT));
            dataTable.addCell(String.format("%.3f", totalGrWt));
            dataTable.addCell(String.format("%.3f", totalStWt));
            dataTable.addCell(String.format("%.3f", totalNetWt));
            dataTable.addCell(String.format("%.3f", totalFine));
            dataTable.addCell(String.format("%.3f", totalStnValue));
            dataTable.addCell(""); // Empty image column in totals row

            document.add(dataTable);

            // Footer
            document.add(new Paragraph("\n\n\n"));
            document.add(new Paragraph("DN Jewellers")
                    .setTextAlignment(TextAlignment.LEFT).setFontSize(10).setBold());
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Note - This is not a Tax Invoice").setBold().setFontSize(10).setFontColor(ColorConstants.RED));

            document.close();

            contentValues.clear();
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(fileUri, contentValues, null, null);

            openPdfFromUri(fileUri);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void savePdfToDownloadFolder7(List<Itemmodel> itemList) {

//        String gstin = "23AAGCV9546B1ZC";
//        String pan = "AAGCV9546B";
//        String heading = "PERFORMA INVOICE";
//        String storename = "VAKPATI JEWELLERS LTD";
//        String add1 = "8/9,SWARNIM COMPLEX, SHOP NO.101,FIRST FLOOR";
//        String add2 = "SHAKKAR BAZAR,SARAFA,OPP.JAIN TEMPLE";
//        String add3 = "INDORE MP 452001";
//        String number = "9109189423";
//
//        String custaddtitle = "Customer Name & Address :";
//        String cname = "SHRI BALAJI GOLD";
//        String cadd1 = "GROUND FLOOR, SHOP NO.1, RODA COMPLEX, SARAFA BAZAR, NAN";
//        String cadd2 = "Nanded, Maharashtra, 431601 NANDED";
//        String cstate = "Maharastra";
//        String cgst = "27ACXFS6666B1ZH";
//        String ccode = "27";
//        String cpan = "ACXFS6666B";
//
//        String billno = "980";
//        String date = "date";
//        String sman = "";
//        String cdheading ="Consignee Detail :";
//        String remark = "RT @999 WT GST 7362 SYS RT 7147.57";
//        String remark1 = "GROUND FLOOR, SHOP NO.1, RODA COMPLEX, SARA";

        String gstin = "23AAGCV9546B1ZC";
        String pan = "AAGCV9546B";
        String heading = "PERFORMA INVOICE";
        String storename = "VAKPATI JEWELLERS LTD.";
        String storeAddress = "8/9, SWARNIM COMPLEX, SHOP NO.101, FIRST FLOOR\n" +
                "SHAKKAR BAZAR, SARAFA, OPP. JAIN TEMPLE\nINDORE MP 452001";
        String contactNumber = "9109189423";
        String billNo = "980";
        String date = "09/09/2024";
        String salesMan = "S-AAYUSHI PANCHAL";

        String customerName = "SHRI BALAJI GOLD";
        String customerAddress = "GROUND FLOOR, SHOP NO.1, RODA COMPLEX, SARAFA BAZAR,\n" +
                "Nanded, Maharashtra, 431601 NANDED";
        String customerState = "Maharastra";
        String customerCode = "27";
        String customerGSTIN = "27ACXFS6666B1ZH";
        String customerPAN = "ACXFS6666B";
        String remark = "RT @999 WT GST 7362 SYS RT 7147.57";
        try {
            String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + "invoiceNumber" + ".pdf";
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(20, 20, 20, 20);
            // Header - GST, PAN, Title, Contact Number
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1}));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            // Left GST & PAN
            Paragraph gstPan = new Paragraph("GSTIN: " + gstin + "\nPAN: " + pan)
                    .setTextAlignment(TextAlignment.LEFT);
            headerTable.addCell(new Cell().add(gstPan).setBorder(Border.NO_BORDER));

            // Center Title
            Paragraph title = new Paragraph(heading)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            headerTable.addCell(new Cell().add(title).setBorder(Border.NO_BORDER));
            // Right Contact Number
            Paragraph number = new Paragraph("Office Copy\n" + contactNumber)
                    .setTextAlignment(TextAlignment.RIGHT);
            headerTable.addCell(new Cell().add(number).setBorder(Border.NO_BORDER));
            // Add header table
            document.add(headerTable);
            // Add Store Name and Address
            Paragraph storeDetails = new Paragraph(storename + "\n" + storeAddress)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(storeDetails);
            Table customerTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
            customerTable.setWidth(UnitValue.createPercentValue(100));
            // Customer Address Block
            Paragraph customerBlock = new Paragraph("Customer Name & Address:\n" + customerName + "\n" + customerAddress)
                    .setBold();
            customerTable.addCell(new Cell().add(customerBlock).setBorder(Border.NO_BORDER));
            // Invoice Details Block
            Paragraph invoiceDetails = new Paragraph("Bill No: " + billNo + "\nDate: " + date + "\nS.Man: " + salesMan)
                    .setTextAlignment(TextAlignment.RIGHT);
            customerTable.addCell(new Cell().add(invoiceDetails).setBorder(Border.NO_BORDER));
            // Add Customer and Invoice details
            document.add(customerTable);
            Table consigneeTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}));
            consigneeTable.setWidth(UnitValue.createPercentValue(100));
            // Left - State, Code, GSTIN, PAN
            Paragraph consigneeLeft = new Paragraph("State: " + customerState + "\nCode: " + customerCode + "\nGSTIN: " + customerGSTIN + "\nPAN: " + customerPAN);
            consigneeTable.addCell(new Cell().add(consigneeLeft).setBorder(Border.NO_BORDER));

            // Right - Remarks and Consignee Details
            Paragraph consigneeRight = new Paragraph("Consignee Detail:\n" + "Remark: " + remark);
            consigneeTable.addCell(new Cell().add(consigneeRight).setBorder(Border.NO_BORDER));

            // Add Consignee details
            document.add(consigneeTable);

            Table itemTable = new Table(UnitValue.createPercentArray(new float[]{
                    1, 4, 1, 2, 2, 2, 2, 2, 2, 2}));
            itemTable.setWidth(UnitValue.createPercentValue(100));
            // Add headers
            itemTable.addHeaderCell("Sno.");
            itemTable.addHeaderCell("Description");
            itemTable.addHeaderCell("Pcs.");
            itemTable.addHeaderCell("Gross Wt.");
            itemTable.addHeaderCell("Net Wt.");
            itemTable.addHeaderCell("Tunch");
            itemTable.addHeaderCell("Wstg");
            itemTable.addHeaderCell("Rate");
            itemTable.addHeaderCell("StoneAmt");
            itemTable.addHeaderCell("Amount (Rupees)");

            // Add sample data rows (Repeat for your data list)
            // Variables to store totals
            double totalGrossWt = 0;
            double totalNetWt = 0;
            double totalAmount = 0;
            int totalpcs = 0;
            double stoneamt = 0;

            for(int i=0; i < itemList.size() ; i++){
                int sn = i+1;
                Itemmodel item = itemList.get(i);

                itemTable.addCell(String.valueOf(sn));
                itemTable.addCell(item.getProduct());
                itemTable.addCell(item.getPcs() != null ? item.getPcs() : "1");
                itemTable.addCell(String.valueOf(item.getGrossWt()));
                itemTable.addCell(String.valueOf(item.getNetWt()));
                itemTable.addCell(String.valueOf(item.getMakingPer()));
                itemTable.addCell(String.valueOf(item.getFixedWastage()));
                itemTable.addCell(String.valueOf(item.getGoldRate()));
                itemTable.addCell(String.valueOf(item.getStoneAmount()));
                double fine = (item.getMakingPer()+item.getFixedWastage())*(item.getNetWt()/100);
                double amt = fine*item.getGoldRate()+item.getStoneAmount();

                itemTable.addCell(decimalFormat.format(amt));

                // Accumulate totals
                totalGrossWt += item.getGrossWt();
                totalNetWt += item.getNetWt();
                totalpcs += Integer.parseInt(item.getPcs() != null ? item.getPcs() : "1");
                totalAmount += amt;
                stoneamt +=  item.getStoneAmount();
            }
            // Add item table to document
//            document.add(itemTable);

            double totalgst = (totalAmount/100)*3;
// Add a total row for all applicable columns
            itemTable.addCell(new Cell(1, 2).add(new Paragraph("Total")));  // Merge first two cells
            itemTable.addCell("");  // Empty cell for Pcs.
            itemTable.addCell(String.format("%.3f", totalGrossWt));  // Total Gross Wt.
            itemTable.addCell(String.format("%.3f", totalNetWt));    // Total Net Wt.
            itemTable.addCell("");  // Empty cell for Tunch
            itemTable.addCell("");  // Empty cell for Wstg
            itemTable.addCell("");  // Empty cell for Rate
            itemTable.addCell(String.format("%.3f", 0.0));    // Total Dia/Stn. (Cts)
            itemTable.addCell(String.format("%.2f", totalAmount));   // Total Amount

            document.add(itemTable);  // Re-add the table with totals

            document.add(new Paragraph("IGST 3.00%").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(decimalFormat.format(totalgst)).setTextAlignment(TextAlignment.RIGHT));

            // Add final total after tax


            document.add(new Paragraph("Total Stone Amt").setTextAlignment(TextAlignment.RIGHT));

            document.add(new Paragraph(String.format("%.2f", stoneamt)).setTextAlignment(TextAlignment.RIGHT));
            double totalAfterTax = totalAmount + totalgst;
            document.add(new Paragraph("Total Amount After Tax").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(String.format("%.2f", totalAfterTax)).setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("HALLMARK CHARGE").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(decimalFormat.format(totalpcs*53.1)).setTextAlignment(TextAlignment.RIGHT));
            double finalBalance = totalAfterTax + (totalpcs*53.1);
            document.add(new Paragraph("Balance Amount").setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph(String.format("%.2f", finalBalance)).setTextAlignment(TextAlignment.RIGHT));

            // Add amount in words
            String amountInWords = NumberToWordsConverter.convertDoubleToWords(finalBalance);
            document.add(new Paragraph(amountInWords).setTextAlignment(TextAlignment.LEFT).setBold());

            document.add(new Paragraph("FOR VAKPATI JEWELLERS LTD."));
            document.add(new Paragraph("2)CHEQUES ARE SUBJECT TO REALISATION.\n" +
                    "SUBJ.TO INDORE JURISDICTION ONLY.\n" +
                    "THIS IS COMPUTER GENERATED INVOICE SIGNATURE NOT REQUIRED"));
            document.close();
            openPdf1(dest);

        }catch (Exception e){
//            Log.e("check error ", Objects.requireNonNull(e.getLocalizedMessage()));
            String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error occurred";
            Log.e("check error", errorMessage, e);
        }


    }

    private void jjjnewpdf(List<Itemmodel> item) throws FileNotFoundException, MalformedURLException {


        String invoiceNumber = item.get(0).getInvoiceNumber();
        long tdate = item.get(0).getOperationTime();
        String cname = item.get(0).getCustomerName();

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + invoiceNumber + ".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);


        String formattedDate = convertTimestampToDate(tdate);

        Paragraph details = new Paragraph("Name :"+item.get(0).getCustomerName()+"\n"+"Order Date :"+formattedDate+"\n"+"Delivery Date :"+formattedDate+"\n"+"Order ID: "+invoiceNumber)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(18);
        document.add(details);


        // Create table with 8 columns
        Table table = new Table(new float[]{1, 3, 2, 2, 2, 2, 2, 4});
        table.setWidth(pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin());

        // Add header cells
        table.addHeaderCell("S.No");
        table.addHeaderCell("Design Code");
        table.addHeaderCell("Gross Wt");
        table.addHeaderCell("Net Wt");
//        table.addHeaderCell("Wastage");
//        table.addHeaderCell("Fine");
        table.addHeaderCell("Amount");
        table.addHeaderCell("Image");

        for (int i = 0; i < item.size(); i++) {
            Itemmodel m = item.get(i);

            // S.No
            table.addCell(String.valueOf(i + 1));

            // Design Code
            table.addCell(m.getItemCode());

            // Gross Wt
            table.addCell(String.valueOf(m.getGrossWt()));

            // Net Wt
            table.addCell(String.valueOf(m.getNetWt()));

            // Wastage (Assuming FixedWastage represents wastage percentage)
//            table.addCell(String.valueOf(m.getFixedWastage()));

            // Fine (Assuming Fine is calculated based on some value)
//            double fine = m.getNetWt() * (m.getFixedWastage() + m.getMakingPer()) / 100;
//            table.addCell(String.valueOf(fine));

            // Amount (Add your calculation for amount here)
            table.addCell(String.valueOf(m.getItemPrice()));

            // Image
            String imageUrl = m.getItemCode() + ".jpg";
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

            if (checkIfFileExists(imageFile)) {
                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image image = new Image(imageData);
                image.setAutoScale(true); // Wrap the image size to fit content
                table.addCell(new Cell().add(image));
            } else {
                table.addCell(new Cell().add(new Paragraph("Image not found")));
            }
        }
        document.add(table);

        /*for(int i = 0; i<item.size(); i++){

            Itemmodel m = item.get(i);
            String no = "";
            if (m.getPartyCode() != null && m.getPartyCode().startsWith("https://jjj.panel")) {
                String fileName = m.getItemCode() + ".jpg";

                // Set the destination file path
                File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + fileName);

                // Check if file exists
                if (!destinationFile.exists()) {
                    no = "image not found";
                } else {
                    no = "image found";
                }
            } else {
                no = m.getPartyCode();
            }

            Paragraph note = new Paragraph("Note  :"+no)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(note);
            Paragraph finewt = new Paragraph("fine+wast  :"+(m.getFixedWastage()+m.getMakingPer()))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(finewt);
            Paragraph total = new Paragraph("total%  :"+(m.getNetWt()*(m.getFixedWastage()+m.getMakingPer()))/100)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(total);

            String imageUrl = m.getItemCode() + ".jpg";
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

            if (checkIfFileExists(imageFile)) {
                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image image = new Image(imageData);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
//                image.scaleToFit(400, 500); // Scale image to fit within the box
                image.setWidth(400);
                image.setHeight(400);
                document.add(image);
            }

            if(i+1 < item.size()){
                document.add(new AreaBreak());
            }



        }*/

        // Add company details


        // Add footer details
//        Paragraph footer = new Paragraph("Delivery To: \nAdvance: \nCustomer Sign.:")
//                .setTextAlignment(TextAlignment.LEFT)
//                .setFontSize(12);
//        document.add(footer);

        document.close();
        openPdf1(dest);



    }

    private List<Itemmodel> getDummyItems() {
        List<Itemmodel> items = new ArrayList<>();

        for (int i = 1; i <= 35; i++) {
            Itemmodel item = new Itemmodel();
            item.setInvoiceNumber("INV000" + i);
            item.setOperationTime(System.currentTimeMillis()); // Current timestamp
            item.setCustomerName("Customer " + i);
            item.setProduct("Item " + i);
            item.setPcs("1"); // Random pieces number
            item.setGrossWt(10 + i * 0.5); // Random gross weight
            item.setStoneWt(2 + i * 0.2); // Random stone weight
            item.setNetWt(8 + i * 0.3); // Random net weight
            item.setItemPrice(5000 + i * 500); // Random item price

            items.add(item);
        }

        return items;
    }

    public void savePdfToDownloadFolder1(List<Itemmodel> item) throws FileNotFoundException {

        String invoiceNumber = item.get(0).getInvoiceNumber();
        long tdate = item.get(0).getOperationTime();
        String cname = item.get(0).getCustomerName();

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + invoiceNumber + ".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Add title
        Paragraph title = new Paragraph("REEVAZ CZ Jewells")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20);
        document.add(title);

        // Add company details
        Paragraph details = new Paragraph("S/4, S/B R. Gala No. 26, 2nd Floor, Thakkar Industrial Estate, Chapsal Bhimji Marg, Old Anjirwadi, Mazgaon, Mumbai - 400 010. Tel: 022-2375 8155 / 9321 174011 / 93200 52877. E-mail: info@reevaz.co.in - www.reevaz.co.in")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10);
        document.add(details);

        // Add invoice details
        Paragraph invoiceDetails = new Paragraph("No. " + invoiceNumber + "\nDate: " + new java.util.Date(tdate).toString())
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(invoiceDetails);

        // Add client details
        Paragraph clientDetails = new Paragraph("Client Name: " + cname +   "   Phone:________ Area:_________ Melting:______ \nDistributor:_____ ")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(clientDetails);

        // Create a table with the appropriate number of columns
        float[] columnWidths = {1, 8, 7, 6, 6, 6, 6};//, 1, 5, 3};
        Table table = new Table(columnWidths);
        table.setWidth(100);

        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Sr. No.")));
        table.addHeaderCell(new Cell().add(new Paragraph("Item Name")));
//        table.addHeaderCell(new Cell().add(new Paragraph("Description")));
        table.addHeaderCell(new Cell().add(new Paragraph("Design No.")));
        table.addHeaderCell(new Cell().add(new Paragraph("Gross Wt")));
        table.addHeaderCell(new Cell().add(new Paragraph("Less Wt")));
        table.addHeaderCell(new Cell().add(new Paragraph("Net Wt")));
        table.addHeaderCell(new Cell().add(new Paragraph("Melting")));


        int tq = 0;
        double tg = 0;
        double ts = 0;
        double tn = 0;


        // Add empty rows to match the image
        for (int i = 0; i < item.size(); i++) {
            Itemmodel it = item.get(i);
            tq++;
            tg = tg+it.getGrossWt();
            ts   = ts+it.getStoneWt();
            tn = tn+it.getNetWt();
            String melt = "";
            if(it.getDescription() != null){
                melt = String.valueOf(it.getDescription());
            }
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
            table.addCell(new Cell().add(new Paragraph(it.getProduct())));
            table.addCell(new Cell().add(new Paragraph(it.getItemCode())));
            table.addCell(new Cell().add(new Paragraph(decimalFormat.format(it.getGrossWt()))));
            table.addCell(new Cell().add(new Paragraph(decimalFormat.format(it.getGrossWt()))));
            table.addCell(new Cell().add(new Paragraph(decimalFormat.format(it.getGrossWt()))));
            table.addCell(new Cell().add(new Paragraph(melt)));
//            table.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
//            table.addCell(new Cell());
//            table.addCell(new Cell());
        }

        // Add table to document
        document.add(table);


        Paragraph total = new Paragraph("Total: " + tq + "  G Wt  :"+tg+"  Less  :"+ts+"  N wt  :"+tn)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(total);

        // Add footer details
        Paragraph footer = new Paragraph("Delivery To: \nAdvance: \nCustomer Sign.:")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(footer);

        document.close();
        openPdf1(dest);
    }


    public void savePdfToDownloadFolder2(List<Itemmodel> item) throws FileNotFoundException, MalformedURLException {
        if (item == null || item.isEmpty()) return;

        String invoiceNumber = item.get(0).getInvoiceNumber();
        long tdate = item.get(0).getOperationTime();
        String cname = item.get(0).getCustomerName();

        if (invoiceNumber == null || invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice";
        }
        if (cname == null || cname.isEmpty()) {
            cname = "customer";
        }

        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + "/" + cname + "_" + invoiceNumber + ".pdf";

        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        Paragraph header = new Paragraph("Bill Report")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(18);
        document.add(header);

        for (int i = 0; i < item.size(); i++) {
            Itemmodel m = item.get(i);

            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

            // Left aligned details
            Paragraph leftDetails = new Paragraph("Customer Name  : " + safe(m.getCustomerName())
                    + "\nOrder No  : " + safe(m.getInvoiceNumber())
                    + "\nItemcode  : " + safe(m.getItemCode())
                    + "\nNotes  : " + safe(m.getPartyCode()))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(14);

            // Right aligned weights
            Paragraph rightDetails = new Paragraph("G wt  : " + safe(String.valueOf(m.getGrossWt()))
                    + "\nS wt  : " + safe(String.valueOf(m.getStoneWt()))
                    + "\nN Wt  : " + safe(String.valueOf(m.getNetWt())))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14);

            table.addCell(leftDetails);
            table.addCell(rightDetails);
            document.add(table);

            // Handle Image
            try {
                String imageUrlStr = m.getImageUrl();
                if (imageUrlStr != null && !imageUrlStr.trim().isEmpty()) {
                    String[] split = imageUrlStr.split(",");
                    String lastImage = split[split.length - 1].trim();
                    String fullImageUrl = "https://rrgold.loyalstring.co.in/" + lastImage;

                    ImageData imageData = ImageDataFactory.create(fullImageUrl);
                    Image image = new Image(imageData);
                    image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    image.setWidth(400);
                    image.setHeight(500);
                    document.add(image);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Optional: log or handle image loading failure
            }

            if (i + 1 < item.size()) {
                document.add(new AreaBreak());
            }
        }

        document.close();
        openPdf1(dest);
    }

    // Utility to handle null strings safely
    private String safe(String input) {
        return input != null ? input : "";
    }
    public void savePdfToDownloadFolder3(List<Itemmodel> item) throws FileNotFoundException, MalformedURLException {

        String invoiceNumber = item.get(0).getInvoiceNumber();
        long tdate = item.get(0).getOperationTime();
        String cname = item.get(0).getCustomerName();

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + invoiceNumber + ".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);


        for(int i = 0; i<item.size(); i++){

            Itemmodel m = item.get(i);
            Paragraph details = new Paragraph("ITEM NAME  \n"+m.getProduct())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(details);

//            Paragraph partyname = new Paragraph("PARTY NAME  \n"+m.getCustomerName())
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFontSize(18);
//            document.add(partyname);

            Paragraph tgwt = new Paragraph("G Wt  :"+m.getGrossWt()+"  S Wt  :"+m.getStoneWt()+"  N Wt  :"+m.getNetWt()+"  S Amount  :"+m.getStoneAmount())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(tgwt);


            String no = "";
            if (m.getPartyCode() != null && m.getPartyCode().startsWith("https://jjj.panel")) {
                String fileName = m.getItemCode() + ".jpg";

                // Set the destination file path
                File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + fileName);

                // Check if file exists
                if (!destinationFile.exists()) {
                    no = "image not found";
                } else {
                    no = "image found";
                }
            } else {
                no = m.getPartyCode();
            }

            Paragraph note = new Paragraph("Note  :"+no)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(note);
            Paragraph finewt = new Paragraph("fine+wast  :"+(m.getFixedWastage()+m.getMakingPer()))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(finewt);
            Paragraph total = new Paragraph("total%  :"+(m.getNetWt()*(m.getFixedWastage()+m.getMakingPer()))/100)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(total);

            String imageUrl = m.getItemCode() + ".jpg";
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

            if (checkIfFileExists(imageFile)) {
                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image image = new Image(imageData);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
//                image.scaleToFit(400, 500); // Scale image to fit within the box
                image.setWidth(400);
                image.setHeight(400);
                document.add(image);
            }

            if(i+1 < item.size()){
                document.add(new AreaBreak());
            }



        }

        // Add company details


        // Add footer details
//        Paragraph footer = new Paragraph("Delivery To: \nAdvance: \nCustomer Sign.:")
//                .setTextAlignment(TextAlignment.LEFT)
//                .setFontSize(12);
//        document.add(footer);

        document.close();
        openPdf1(dest);
    }


    public static void generatePdf(String dest, HashMap<String, List<Itemmodel>> billmap) throws IOException {
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        document.add(new Paragraph("Header"));

        // 3 groups per row
        Table table = new Table(3);

        for (String itemCode : billmap.keySet()) {
            List<Itemmodel> items = billmap.get(itemCode);

            for (Itemmodel item : items) {
                Cell cell = new Cell();
                cell.add(new Paragraph(item.getItemCode()));

                // Add the image

                String imageUrlString = item.getImageUrl(); // e.g., "img1.jpg,img2.jpg,img3.jpg"
                String onlineimage="";
                if (imageUrlString != null && !imageUrlString.isEmpty()) {
                    String[] imageUrls = imageUrlString.split(",");
                    String lastImage = imageUrls[imageUrls.length - 1].trim(); // get last and trim spaces
                    onlineimage = "https://rrgold.loyalstring.co.in/" + lastImage;
                    // Use `onlineImage` as needed
                } else {
                    // fallback or placeholder
                    onlineimage = "https://rrgold.loyalstring.co.in/default.jpg";
                }

                ImageData imageData = ImageDataFactory.create(onlineimage); // pass URL
                Image image = new Image(imageData);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                image.setWidth(400);
                image.setHeight(500);
                document.add(image);

                // Add the cell to the table
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
    }

    private void savePdfToDownloadFolder(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        // Add Header
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        String formattedDate = convertTimestampToDate(tdate);

        Table headerTable = new Table(2);
        headerTable.setWidth(pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin());

        Cell headerCell1 = new Cell();
        headerCell1.add(new Paragraph(cname));
        headerCell1.add(new Paragraph("Email: gamila.com"));
        headerCell1.setBorder(Border.NO_BORDER);
        headerCell1.setTextAlignment(TextAlignment.LEFT);

        Cell headerCell2 = new Cell();
        headerCell2.add(new Paragraph("Date: " + formattedDate));
        headerCell2.add(new Paragraph("Status: Order Summary"));
        headerCell2.setBorder(Border.NO_BORDER);
        headerCell2.setTextAlignment(TextAlignment.RIGHT);

        headerTable.addCell(headerCell1);
        headerTable.addCell(headerCell2);

        document.add(headerTable);

        LineSeparator dashedLine = new LineSeparator(new CustomDashedLine(3f));
        document.add(new Paragraph().add(dashedLine));

        // Add table with items, 3 items per row
        float availableWidth = pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();
        float cellWidth = availableWidth / 3; // Divide available width by 3 for equal columns
        float cellHeight = 250; // Set cell height
        float pageHeight = pdfDoc.getDefaultPageSize().getHeight() - document.getTopMargin() - document.getBottomMargin();
        float usedHeight = 0; // Track the height used
        Log.e("checking height", " "+pageHeight);

        Table itemTable = new Table(3);
        itemTable.setWidth(UnitValue.createPercentValue(100));

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        for (String itemCode : billmap.keySet()) {
            List<Itemmodel> items = billmap.get(itemCode);
            double tgross = 0;
            double tnet = 0;
            String it = "";
            String onlineimage = "";
            String imageUrlString = "";
            for (Itemmodel item : items) {
                tgross = tgross + item.getGrossWt();
                tnet = tnet + item.getNetWt();
                it = it + item.getProduct() + " G Wt " + decimalFormat.format(item.getGrossWt()) + " N Wt " + decimalFormat.format(item.getNetWt()) + "\n";

                imageUrlString = item.getImageUrl();
            }

            Cell itemCell = new Cell();
            itemCell.setPadding(10);
            itemCell.add(new Paragraph(itemCode));
            itemCell.add(new Paragraph("T GWt "+decimalFormat.format(tgross) + "  T NWt " + decimalFormat.format(tnet)));

            String imageUrl = itemCode + ".jpg";
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);


           // e.g., "img1.jpg,img2.jpg,img3.jpg"
            if (imageUrlString != null && !imageUrlString.isEmpty()) {
                String[] imageUrls = imageUrlString.split(",");
                String lastImage = imageUrls[imageUrls.length - 1].trim(); // get last and trim spaces
                onlineimage = "https://rrgold.loyalstring.co.in/" + lastImage;
                // Use `onlineImage` as needed
            } else {
                // fallback or placeholder
                onlineimage = "https://rrgold.loyalstring.co.in/default.jpg";
            }


            try {
                ImageData imageData = ImageDataFactory.create(onlineimage); // URL-based
                Image image = new Image(imageData);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                image.setWidth(400);
                image.setHeight(500);
                document.add(image);
            } catch (Exception e) {
                Log.e("PDF Image Load", "Failed to load image from URL: " + onlineimage, e);
            }

            itemCell.add(new Paragraph(it));
            itemCell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            itemCell.setWidth(cellWidth); // Set width for equal columns
            itemCell.setHeight(cellHeight); // Set height for equal rows
            itemCell.setHorizontalAlignment(HorizontalAlignment.CENTER);

            itemCell.setVerticalAlignment(VerticalAlignment.MIDDLE); // Center content vertically

            // Check if adding the cell would overflow the page height
//            float rowHeight = itemCell.getHeight() + 10; // Adding padding

            // Check if adding this row will overflow the page
            if (usedHeight == 6) {
                document.add(itemTable); // Add the table to the document
                itemTable = new Table(3); // Create a new table for the next page
                itemTable.setWidth(UnitValue.createPercentValue(100));
                document.add(new AreaBreak()); // Force a new page
                usedHeight = 0; // Reset used height
            }

            itemTable.addCell(itemCell);
            usedHeight++; // Update used height
        }

        // Add remaining cells if any
//        if (itemTable.getNumberOfCells() > 0) {
        document.add(itemTable);
//        }



        document.close();

        // Open the PDF after saving
        openPdf1(dest);
    }


    private void savemvspdf(List<Itemmodel> items) throws FileNotFoundException {

        String invoiceNumber = items.get(0).getInvoiceNumber();
        long tdate = items.get(0).getOperationTime();
        String cname = items.get(0).getCustomerName();
        tdate = items.get(0).getOperationTime();

        String formattedDate = convertTimestampToDate(tdate);

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + invoiceNumber + ".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        Table headerTable = new Table(2);
        headerTable.setWidth(pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin());

        Cell headerCell1 = new Cell();
        headerCell1.add(new Paragraph(cname));
        headerCell1.add(new Paragraph("Email: gamila.com"));
        headerCell1.setBorder(Border.NO_BORDER);
        headerCell1.setTextAlignment(TextAlignment.LEFT);

        Cell headerCell2 = new Cell();
        headerCell2.add(new Paragraph("Date: " +formattedDate));
        headerCell2.add(new Paragraph("Status: Order Summary"));
        headerCell2.setBorder(Border.NO_BORDER);
        headerCell2.setTextAlignment(TextAlignment.RIGHT);

        headerTable.addCell(headerCell1);
        headerTable.addCell(headerCell2);

        document.add(headerTable);


        // Items Table
        Table itemTable = new Table(new float[]{1, 3, 1, 1, 1, 1, 2}); // Adjust column widths
        itemTable.setWidth(pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin());
        itemTable.addHeaderCell("S.No");
        itemTable.addHeaderCell("Item Name");
        itemTable.addHeaderCell("Pcs");
        itemTable.addHeaderCell("Gross Wt");
        itemTable.addHeaderCell("Stone Wt");
        itemTable.addHeaderCell("Net Wt");
        itemTable.addHeaderCell("Amount");

        double totalGross = 0, totalStone = 0, totalNet = 0;

        Map<String, ItemTotal> totalsMap = new HashMap<>();
        for (int i = 0; i < items.size(); i++) {
            Itemmodel item = items.get(i);

            // Fill item data into the table
            itemTable.addCell(String.valueOf(i + 1)); // S.No
            itemTable.addCell(item.getProduct());    // Item Name
            itemTable.addCell(String.valueOf(item.getPcs())); // Pcs
            itemTable.addCell(String.valueOf(item.getGrossWt())); // Gross Wt
            itemTable.addCell(String.valueOf(item.getStoneWt())); // Stone Wt
            itemTable.addCell(String.valueOf(item.getNetWt())); // Net Wt
            itemTable.addCell(String.valueOf(item.getItemPrice())); // Amount

            // Accumulate totals
//            totalGross += item.getGrossWt();
//            totalStone += item.getStoneWt();
//            totalNet += item.getNetWt();

            String productName = item.getProduct();
            if (totalsMap.containsKey(productName)) {
                // If the product already exists in the map, update its totals
                ItemTotal itemTotal = totalsMap.get(productName);
                int pcs = (item.getPcs() != null && !item.getPcs().isEmpty()) ? Integer.parseInt(item.getPcs()) : 0;

                itemTotal.add(pcs, item.getGrossWt(), item.getNetWt(), item.getStoneWt());
            } else {
                // If the product does not exist, create a new entry
                int pcs = (item.getPcs() != null && !item.getPcs().isEmpty()) ? Integer.parseInt(item.getPcs()) : 0;

                totalsMap.put(productName, new ItemTotal(pcs, item.getGrossWt(), item.getNetWt(), item.getStoneWt()));
            }


        }

        document.add(itemTable);


        document.add(new Paragraph("\n"));
        // Create a new table for totals by item name
        Table totalTable = new Table(new float[]{3, 1, 1, 1}); // Adjust column widths
        totalTable.setWidth(pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin());

        // Add headers for totals table
        totalTable.addHeaderCell("Item Name");
        totalTable.addHeaderCell("Total Pcs");
        totalTable.addHeaderCell("Total Gross Wt");
        totalTable.addHeaderCell("Total Net Wt");

        // Iterate through the totals map and add each product's totals to the table
        for (Map.Entry<String, ItemTotal> entry : totalsMap.entrySet()) {
            String productName = entry.getKey();
            ItemTotal total = entry.getValue();

            totalTable.addCell(productName); // Item Name
            totalTable.addCell(String.valueOf(total.totalPcs)); // Total Pcs
            totalTable.addCell(String.valueOf(total.totalGrossWt)); // Total Gross Wt
            totalTable.addCell(String.valueOf(total.totalNetWt)); // Total Net Wt
        }

        document.add(totalTable);


        document.close();
        openPdf1(dest);

    }

    private void openPdf(String filePath) {
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    private void openPdf1(String filePath) {
        File file = new File(filePath);
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // If no app is available to view PDF files
            Toast.makeText(context, "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
        /*Uri uri = Uri.fromFile(file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);*/
    }

    private boolean checkIfFileExists(File file) {
        if (file.exists()) {
            Log.d("FileCheck", "File exists: " + file.getAbsolutePath());
            return true;
        } else {
            Log.d("FileCheck", "File does not exist: " + file.getAbsolutePath());
            return false;
        }
    }

    private static class CustomDashedLine extends DashedLine {
        public CustomDashedLine(float lineWidth) {
            super(lineWidth);
        }

        @Override
        public void draw(PdfCanvas canvas, Rectangle drawArea) {
            canvas.saveState()
                    .setLineWidth(getLineWidth())
                    .setStrokeColor(getColor())
                    .setLineDash(20, 4, 2)
                    .moveTo(drawArea.getX(), drawArea.getY() + getLineWidth() / 2)
                    .lineTo(drawArea.getX() + drawArea.getWidth(), drawArea.getY() + getLineWidth() / 2)
                    .stroke()
                    .restoreState();
        }
    }


    class ItemTotal {
        int totalPcs;
        double totalGrossWt;
        double totalNetWt;
        double totalStoneWt;

        public ItemTotal(int pcs, double grossWt, double netWt, double stoneWt) {
            this.totalPcs = pcs;
            this.totalGrossWt = grossWt;
            this.totalNetWt = netWt;
            this.totalStoneWt = stoneWt;
        }

        public void add(int pcs, double grossWt, double netWt, double stoneWt) {
            this.totalPcs += pcs;
            this.totalGrossWt += grossWt;
            this.totalNetWt += netWt;
            this.totalStoneWt += stoneWt;
        }
    }



    //pushpa jewellery
    private void pushpa1(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        String branch = "";
        String via = "";
        String kt = "";
        String screw = "";

        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                branch = entry.getValue().get(0).getBranch();
                via = entry.getValue().get(0).getDiamondCertificate();
                kt = entry.getValue().get(0).getDiamondClarity();
                screw = entry.getValue().get(0).getDiamondColor();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        String formattedDate = convertTimestampToDate(tdate);


//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pdf); // Replace with your drawable resource
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        ImageData imageData = ImageDataFactory.create(byteArray);
//        Image image = new Image(imageData).setWidth(100).setHeight(50); // Adjust size as needed
//        document.add(image);

        // Add Header Section
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left Header Content
        Cell leftHeader = new Cell();
        leftHeader.add(new Paragraph("DATE:"+formattedDate).setBold());
        leftHeader.add(new Paragraph("CLIENT NAME:"+cname).setBold());
        leftHeader.add(new Paragraph("NAME & PHN NO:").setBold());
        leftHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(leftHeader);

        // Right Header Content
        Cell rightHeader = new Cell();
        rightHeader.add(new Paragraph("KT: "+kt).setBold());
        rightHeader.add(new Paragraph("SCREW: "+screw).setBold());
        rightHeader.add(new Paragraph("SEPARATE TAGS YES/NO").setBold());
        rightHeader.add(new Paragraph("WASTAGE").setBold());
        rightHeader.add(new Paragraph("DELIVERY DATE").setBold());
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(rightHeader);

        document.add(headerTable);
        float[] columnWidths = {1, 3, 2, 2, 2}; // Adjust column widths
        Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
        dataTable.setWidth(UnitValue.createPercentValue(100));

        // Add Table Header
        dataTable.addHeaderCell(new Cell().add(new Paragraph("SR NO").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("ITEM NAME").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("DESIGN NO.").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NO. OF PCS").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NW").setBold()));

        int i =0 ;

        int totalPcs = 0;
        double totalNW = 0.0f;

        // Add Rows Dynamically
        for (String it : billmap.keySet()){ // Example for multiple rows
            List<Itemmodel> items = billmap.get(it);
            for(Itemmodel item : items){
                i++;

                int pcs = 1; // Assuming each item has 1 piece
                double nw = item.getNetWt();


                dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
                dataTable.addCell(new Cell().add(new Paragraph(item.getProduct())));
                dataTable.addCell(new Cell().add(new Paragraph(item.getDiamondClarity())));
                dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(1))));
                dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getNetWt()))));

                totalPcs += pcs;
                totalNW += nw;
            }

        }
        Cell totalLabelCell = new Cell(1, 3).add(new Paragraph("TOTAL").setBold());
        totalLabelCell.setTextAlignment(TextAlignment.RIGHT);
        dataTable.addCell(totalLabelCell);
        dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalPcs)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(decimalFormat.format(totalNW)).setBold()));
        document.add(dataTable);

        // Bank Details Footer
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.addCell(new Cell().add(new Paragraph("CASH:").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("BANK: ABC Bank").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("RTGS DETAILS: 12345678").setBold()).setBorder(Border.NO_BORDER));

        // Add Footer Content
        footerTable.addCell(new Cell().add(new Paragraph("COMPANY INFO: ABC Pvt Ltd, Contact: 9876543210, Email: info@company.com"))
                .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        document.add(footerTable);

        // Add Header
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

//        String formattedDate = convertTimestampToDate(tdate);
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }

    /*private void pushpa11(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        String branch = "";
        String via = "";
        String kt = "";
        String screw = "";
        String tags = "";
        String wast = "";

        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                branch = entry.getValue().get(0).getBranch();
                via = entry.getValue().get(0).getDiamondCertificate();
                kt = entry.getValue().get(0).getStockKeepingUnit();
                screw = entry.getValue().get(0).getDiamondColor();
                tags = entry.getValue().get(0).getDiamondMetal();
                wast = String.valueOf(entry.getValue().get(0).getFixedWastage());
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4.rotate());

        String formattedDate = convertTimestampToDate(tdate);
        // Add Header Section

        Paragraph title = new Paragraph("Proforma Invoice")
                .setBold()
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

// Add some space after the title
        document.add(new Paragraph("\n"));

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left Header Content
        Cell leftHeader = new Cell();
        leftHeader.add(new Paragraph("DATE:"+formattedDate).setBold());
        leftHeader.add(new Paragraph("CLIENT NAME:"+cname).setBold());
//        leftHeader.add(new Paragraph("NAME & PHN NO:").setBold());
        leftHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(leftHeader);

        // Right Header Content
        Cell rightHeader = new Cell();
        rightHeader.add(new Paragraph("KT: "+kt).setBold());
        rightHeader.add(new Paragraph("SCREW: "+screw).setBold());
        rightHeader.add(new Paragraph("SEPARATE TAGS: "+tags).setBold());
        rightHeader.add(new Paragraph("WASTAGE "+wast).setBold());
        rightHeader.add(new Paragraph("DELIVERY DATE").setBold());
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(rightHeader);

        document.add(headerTable);
        // Define new column widths based on the new structure
//        float[] columnWidths = {1f, 2f, 2f, 2f, 2f, 2f, 2f, 1.5f, 1.5f, 2f}; // Adjust column widths
//        float[] columnWidths = {1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f,1f,1f,1f }; // Increased last column width
//        Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
//        dataTable.setWidth(UnitValue.createPercentValue(100));


        float[] columnWidths = {0.5f, 1.0f, 1.5f, 1f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
        dataTable.setWidth(UnitValue.createPercentValue(100));


// Add Table Header
        dataTable.addHeaderCell(new Cell().add(new Paragraph("SNO").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("TAG NO").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("ITEM NAME").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("DESIGN").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("STAMP").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("G WT").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("S WT").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("N WT").setBold()));
//        dataTable.addHeaderCell(new Cell().add(new Paragraph("GOLD").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("FINE").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("STN VALUE").setBold()));

        int i = 0;

        int totalPcs = 0;
        double totalGrWt = 0.0;
        double totalStWt = 0.0;
        double totalNetWt = 0.0;
        double totalGold = 0.0;
        double totalFine = 0.0;
        double totalStnValue = 0.0;

// Add Rows Dynamically
        for (String it : billmap.keySet()) {
            List<Itemmodel> items = billmap.get(it);
            for (Itemmodel item : items) {
                i++;

                // Assuming these are the values you want to add
                int pcs = 1; // Assuming each item has 1 piece
                double grwt = item.getGrossWt();
                double stwt = item.getGrossWt()-item.getNetWt();
                double netwt = item.getNetWt();
                double gold = item.getGoldRate(); // Replace with the actual getter
                double fine = item.getMakingPer(); // Replace with the actual getter
                double stnValue = item.getStoneAmount(); // Replace with the actual getter
                double fine1 = (item.getMakingPer()+item.getFixedWastage())*(item.getNetWt()/100);




                // Add row data to the table
                dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
                dataTable.addCell(new Cell().add(new Paragraph(item.getItemCode())));
                dataTable.addCell(new Cell().add(new Paragraph(item.getProduct())));
                dataTable.addCell(new Cell().add(new Paragraph(item.getDiamondClarity())));
                dataTable.addCell(new Cell().add(new Paragraph(
                        item.getStockKeepingUnit() != null ? item.getStockKeepingUnit() : ""
                )));
                dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", grwt))));
                dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", stwt))));
                dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", netwt))));//netwt
//                dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(gold))));
                dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f",fine1))));
                dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", stnValue))));

                // Accumulate totals
                totalPcs += pcs;
                totalGrWt += grwt;
                totalStWt += stwt;
                totalNetWt += netwt;
                totalGold += gold;
                totalFine += fine1;
                totalStnValue += stnValue;
            }
        }

// Add Total Row
        Cell totalLabelCell = new Cell(1, 5).add(new Paragraph("TOTAL").setBold());
        totalLabelCell.setTextAlignment(TextAlignment.RIGHT);
        dataTable.addCell(totalLabelCell);
//        dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(totalPcs)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalGrWt)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalStWt)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalNetWt)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalFine)).setBold()));
        dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalStnValue)).setBold()));

        document.add(dataTable);



        // Add Footer Section
        Paragraph footer = new Paragraph("PUSHPA JEWELLERS LIMITED\n" +
                "ADDRESS - 4TH floor, Flat 4A, 22 East Topsia Road, Tirumala - 22, Kolkata - 700046\n" +
                "Contact - 9831545491\n" +
                "Email - info@pushpajewellers.in\n" +
                "GST - 19AAFCP0896D1Z9\n\n" +
                "BANK NAME - ICICI BANK LTD.\n" +
                "BANK A/C - 030505005192\n" +
                "BANK IFSC CODE - ICIC0006950")
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(10)
                .setBold();

// Add a Note Below Footer
        Paragraph note = new Paragraph("Note - This is not a Tax Invoice")
                .setBold()
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontColor(ColorConstants.RED);

// Move the footer to the bottom
        document.add(new Paragraph("\n\n\n")); // Adds space before footer
        document.add(footer);
        document.add(new Paragraph("\n")); // Adds space before the note
        document.add(note);



        // Bank Details Footer
//        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
//        footerTable.setWidth(UnitValue.createPercentValue(100));
//        footerTable.addCell(new Cell().add(new Paragraph("CASH:").setBold()).setBorder(Border.NO_BORDER));
//        footerTable.addCell(new Cell().add(new Paragraph("BANK: ABC Bank").setBold()).setBorder(Border.NO_BORDER));
//        footerTable.addCell(new Cell().add(new Paragraph("RTGS DETAILS: 12345678").setBold()).setBorder(Border.NO_BORDER));
//
//        // Add Footer Content
//        footerTable.addCell(new Cell().add(new Paragraph("COMPANY INFO: ABC Pvt Ltd, Contact: 9876543210, Email: info@company.com"))
//                .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
//        document.add(footerTable);

        // Add Header
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

//        String formattedDate = convertTimestampToDate(tdate);
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }*/

    @SuppressLint("Range")
    private void pushpa11(HashMap<String, List<Itemmodel>> billmap, int invoiceId) {
        String invoiceNumber = "";
        long tdate = 0;
        String cname = "", branch = "", via = "", kt = "", screw = "", tags = "", wast = "";

        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                Itemmodel model = entry.getValue().get(0);
                invoiceNumber = model.getInvoiceNumber();
                tdate = model.getOperationTime();
                cname = model.getCustomerName();
                branch = model.getBranch();
                via = model.getDiamondCertificate();
                kt = model.getStockKeepingUnit();
                screw = model.getDiamondColor();
                tags = model.getDiamondMetal();
                wast = String.valueOf(model.getFixedWastage());
                break;
            }
        }

        if (invoiceNumber.isEmpty()) invoiceNumber = "unknown_invoice";

        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Downloads.DISPLAY_NAME, invoiceNumber + ".pdf");
            contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            contentValues.put(MediaStore.Downloads.IS_PENDING, 1);

            Uri collection = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            }
            assert collection != null;
            Uri fileUri = resolver.insert(collection, contentValues);

            if (fileUri == null) {
                Toast.makeText(context, "Failed to create file", Toast.LENGTH_LONG).show();
                return;
            }

            OutputStream outputStream = resolver.openOutputStream(fileUri);

            if (outputStream == null) {
                Toast.makeText(context, "Failed to open output stream", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate PDF
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4.rotate());

            String formattedDate = convertTimestampToDate(tdate);
            Paragraph title = new Paragraph("Proforma Invoice")
                    .setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER);
            document.add(title);
            document.add(new Paragraph("\n"));

            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
            headerTable.setWidth(UnitValue.createPercentValue(100));

            Cell leftHeader = new Cell().setBorder(Border.NO_BORDER);
            leftHeader.add(new Paragraph("DATE: " + formattedDate).setBold());
            leftHeader.add(new Paragraph("CLIENT NAME: " + cname).setBold());
            leftHeader.add(new Paragraph("NAME & PHONE NO: ").setBold());
            headerTable.addCell(leftHeader);

            Cell rightHeader = new Cell().setBorder(Border.NO_BORDER);
            rightHeader.add(new Paragraph("KT: " + kt).setBold());
            rightHeader.add(new Paragraph("SCREW: " + screw).setBold());
            rightHeader.add(new Paragraph("SEPARATE TAGS: " + tags).setBold());
            rightHeader.add(new Paragraph("WASTAGE " + wast).setBold());
            rightHeader.add(new Paragraph("DELIVERY DATE").setBold());
            rightHeader.setTextAlignment(TextAlignment.RIGHT);
            headerTable.addCell(rightHeader);

            document.add(headerTable);

            float[] columnWidths = {0.5f, 1.0f, 1.5f, 1f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
            dataTable.setWidth(UnitValue.createPercentValue(100));

            dataTable.addHeaderCell(new Cell().add(new Paragraph("SNO").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("TAG NO").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("ITEM NAME").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("DESIGN").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("STAMP").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("G WT").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("S WT").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("N WT").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("FINE").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("STN VALUE").setBold()));

            int i = 0;
            double totalGrWt = 0.0, totalStWt = 0.0, totalNetWt = 0.0, totalFine = 0.0, totalStnValue = 0.0;

            for (String it : billmap.keySet()) {
                List<Itemmodel> items = billmap.get(it);
                for (Itemmodel item : items) {
                    i++;
                    double grwt = item.getGrossWt();
                    double netwt = item.getNetWt();
                    double stwt = grwt - netwt;
                    double fine1 = (item.getMakingPer() + item.getFixedWastage()) * (netwt / 100);
                    double stnValue = item.getStoneAmount();

                    dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
                    dataTable.addCell(new Cell().add(new Paragraph(item.getItemCode())));
                    dataTable.addCell(new Cell().add(new Paragraph(item.getProduct())));
                    if(item.getDiamondClarity()!=null) {
                        dataTable.addCell(new Cell().add(new Paragraph(item.getDiamondClarity())));
                    }else {
                        dataTable.addCell(new Cell().add(new Paragraph("")));
                    }
                    dataTable.addCell(new Cell().add(new Paragraph(item.getStockKeepingUnit() != null ? item.getStockKeepingUnit() : "")));
                    dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", grwt))));
                    dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", stwt))));
                    dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", netwt))));
                    dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", fine1))));
                    dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", stnValue))));

                    totalGrWt += grwt;
                    totalStWt += stwt;
                    totalNetWt += netwt;
                    totalFine += fine1;
                    totalStnValue += stnValue;
                }
            }

            Cell totalLabelCell = new Cell(1, 5).add(new Paragraph("TOTAL").setBold());
            totalLabelCell.setTextAlignment(TextAlignment.RIGHT);
            dataTable.addCell(totalLabelCell);
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalGrWt)).setBold()));
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalStWt)).setBold()));
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalNetWt)).setBold()));
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalFine)).setBold()));
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.3f", totalStnValue)).setBold()));

            document.add(dataTable);
            Paragraph footer;
            if (invoiceId == 43) {
                footer = new Paragraph("PAARVAI JEWELLERS\n")
                        .setTextAlignment(TextAlignment.LEFT).setFontSize(10).setBold();

            } else if (invoiceId == 44) {
                footer = new Paragraph("REEVAZZ JEWELLERS\n")
                        .setTextAlignment(TextAlignment.LEFT).setFontSize(10).setBold();
            } else {
                footer = new Paragraph("PUSHPA JEWELLERS LIMITED\n" +
                        "ADDRESS - 4TH floor, Flat 4A, 22 East Topsia Road, Tirumala - 22, Kolkata - 700046\n" +
                        "Contact - 9831545491\n" +
                        "Email - info@pushpajewellers.in\n" +
                        "GST - 19AAFCP0896D1Z9\n\n" +
                        "BANK NAME - ICICI BANK LTD.\n" +
                        "BANK A/C - 030505005192\n" +
                        "BANK IFSC CODE - ICIC0006950")
                        .setTextAlignment(TextAlignment.LEFT).setFontSize(10).setBold();
            }



            Paragraph note = new Paragraph("Note - This is not a Tax Invoice")
                    .setBold().setFontSize(10)
                    .setTextAlignment(TextAlignment.LEFT).setFontColor(ColorConstants.RED);

            document.add(new Paragraph("\n\n\n"));
            document.add(footer);
            document.add(new Paragraph("\n"));
            document.add(note);
            document.close();

            contentValues.clear();
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(fileUri, contentValues, null, null);

            openPdfFromUri(fileUri);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openPdfFromUri(Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);  // Auto-open directly
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }



    private void openPdf11(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    file
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

            context.startActivity(Intent.createChooser(intent, "Open PDF"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to open PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }





    private void pushpa2(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);


//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pdf); // Replace with your drawable resource
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        ImageData imageData = ImageDataFactory.create(byteArray);
//        Image image = new Image(imageData).setWidth(100).setHeight(50); // Adjust size as needed
//        document.add(image);

        // Add Header Section
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left Header Content
        Cell leftHeader = new Cell();
        leftHeader.add(new Paragraph("DATE:").setBold());
        leftHeader.add(new Paragraph("CLIENT NAME:").setBold());
        leftHeader.add(new Paragraph("NAME & PHN NO:").setBold());
        leftHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(leftHeader);

        // Right Header Content
        Cell rightHeader = new Cell();
        rightHeader.add(new Paragraph("KT").setBold());
        rightHeader.add(new Paragraph("SCREW").setBold());
        rightHeader.add(new Paragraph("SEPARATE TAGS YES/NO").setBold());
        rightHeader.add(new Paragraph("WASTAGE").setBold());
        rightHeader.add(new Paragraph("DELIVERY DATE").setBold());
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(rightHeader);

        document.add(headerTable);
        float[] columnWidths = {1, 3, 2, 2, 2}; // Adjust column widths
        Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
        dataTable.setWidth(UnitValue.createPercentValue(100));

        // Add Table Header
        dataTable.addHeaderCell(new Cell().add(new Paragraph("SR NO").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("ITEM NAME").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("DESIGN NO.").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NO. OF PCS").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NW").setBold()));

        // Add Rows Dynamically
        for (int i = 1; i <= 20; i++) { // Example for multiple rows
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            dataTable.addCell(new Cell().add(new Paragraph("Item " + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Design " + i)));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i * 2))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i * 1.5))));
        }

        document.add(dataTable);

        // Bank Details Footer
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.addCell(new Cell().add(new Paragraph("CASH:").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("BANK: ABC Bank").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("RTGS DETAILS: 12345678").setBold()).setBorder(Border.NO_BORDER));

        // Add Footer Content
        footerTable.addCell(new Cell().add(new Paragraph("COMPANY INFO: ABC Pvt Ltd, Contact: 9876543210, Email: info@company.com"))
                .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        document.add(footerTable);

        // Add Header
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        String formattedDate = convertTimestampToDate(tdate);
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }

    private void pushpa3(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);


// Set margins for the document
        document.setMargins(20, 20, 20, 20);

        // Header Section
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        Cell leftHeader = new Cell();
        leftHeader.add(new Paragraph("Date: " + new SimpleDateFormat("dd-MM-yyyy").format(new Date())).setBold());
        leftHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(leftHeader);

        Cell rightHeader = new Cell();
        rightHeader.add(new Paragraph("Ref No: REF12345").setBold());
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(rightHeader);

        document.add(headerTable);

        // Add Gap
        document.add(new Paragraph("\n"));

        // Table Header Section
        Table dataTable = new Table(UnitValue.createPercentArray(new float[]{1, 2, 3, 3, 2, 2, 2, 2, 2, 3}));
        dataTable.setWidth(UnitValue.createPercentValue(100));

        String[] tableHeaders = {
                "Sr. No.", "TAG NO", "ITEM NAME", "Design", "Stamp", "Gr.Wt.",
                "Stn.Wt.", "Net.Wt.", "Gold Fine", "STN VALUE"
        };

        for (String header : tableHeaders) {
            Cell headerCell = new Cell();
            headerCell.add(new Paragraph(header).setBold());
            headerCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerCell.setTextAlignment(TextAlignment.CENTER);
            dataTable.addHeaderCell(headerCell);
        }

        // Add Rows Dynamically
        for (int i = 1; i <= 20; i++) { // Example rows
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            dataTable.addCell(new Cell().add(new Paragraph("TAG" + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Item Name " + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Design" + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Stamp" + i)));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(10 * i))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(5 * i))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(15 * i))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(20 * i))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(25 * i))));
        }

        document.add(dataTable);

        // Footer Section
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.addCell(new Cell().add(new Paragraph("XXXX XXXX XXXX XXXX XXXX"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setBorder(Border.NO_BORDER));

        document.add(footerTable);

        // Close the document
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }

    private void pushpa4(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

        // Set margins
        document.setMargins(20, 20, 20, 20);

        // Client Name Section
        String clientName = "CLIENT NAME: John Doe";
        document.add(new Paragraph(clientName)
                .setBold()
                .setFontSize(12)
                .setTextAlignment(TextAlignment.LEFT));

        // Add Gap
        document.add(new Paragraph("\n"));

        // Table Header Section
        Table dataTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 3, 2, 2}));
        dataTable.setWidth(UnitValue.createPercentValue(100));

        String[] tableHeaders = {
                "Sr. No.", "ITEM NAME", "Design", "Stamp", "Net.Wt."
        };

        for (String header : tableHeaders) {
            Cell headerCell = new Cell();
            headerCell.add(new Paragraph(header).setBold());
            headerCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            headerCell.setTextAlignment(TextAlignment.CENTER);
            dataTable.addHeaderCell(headerCell);
        }

        // Add Rows Dynamically
        for (int i = 1; i <= 6; i++) { // Example rows
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            dataTable.addCell(new Cell().add(new Paragraph("Item Name " + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Design " + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Stamp " + i)));
            dataTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", (10.5 * i)))));
        }

        // Add Table to Document
        document.add(dataTable);

        // Footer Section
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("XXXX")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(10));

        // Close the document
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }

    private void pushpa5(HashMap<String, List<Itemmodel>> billmap) throws IOException {

        String invoiceNumber = "";
        long tdate = 0;
        String cname = "";
        for (Map.Entry<String, List<Itemmodel>> entry : billmap.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                invoiceNumber = entry.getValue().get(0).getInvoiceNumber();
                tdate = entry.getValue().get(0).getOperationTime();
                cname = entry.getValue().get(0).getCustomerName();
                break;
            }
        }

        if (invoiceNumber.isEmpty()) {
            invoiceNumber = "unknown_invoice"; // Default if no invoice number found
        }
        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/" + invoiceNumber+".pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);


//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pdf); // Replace with your drawable resource
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//
//        ImageData imageData = ImageDataFactory.create(byteArray);
//        Image image = new Image(imageData).setWidth(100).setHeight(50); // Adjust size as needed
//        document.add(image);

        // Add Header Section
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left Header Content
        Cell leftHeader = new Cell();
        leftHeader.add(new Paragraph("DATE:").setBold());
        leftHeader.add(new Paragraph("CLIENT NAME:").setBold());
        leftHeader.add(new Paragraph("NAME & PHN NO:").setBold());
        leftHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(leftHeader);

        // Right Header Content
        Cell rightHeader = new Cell();
        rightHeader.add(new Paragraph("KT").setBold());
        rightHeader.add(new Paragraph("SCREW").setBold());
        rightHeader.add(new Paragraph("SEPARATE TAGS YES/NO").setBold());
        rightHeader.add(new Paragraph("WASTAGE").setBold());
        rightHeader.add(new Paragraph("DELIVERY DATE").setBold());
        rightHeader.setTextAlignment(TextAlignment.RIGHT);
        rightHeader.setBorder(Border.NO_BORDER);
        headerTable.addCell(rightHeader);

        document.add(headerTable);
        float[] columnWidths = {1, 3, 2, 2, 2}; // Adjust column widths
        Table dataTable = new Table(UnitValue.createPercentArray(columnWidths));
        dataTable.setWidth(UnitValue.createPercentValue(100));

        // Add Table Header
        dataTable.addHeaderCell(new Cell().add(new Paragraph("SR NO").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("ITEM NAME").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("DESIGN NO.").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NO. OF PCS").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("NW").setBold()));

        // Add Rows Dynamically
        for (int i = 1; i <= 20; i++) { // Example for multiple rows
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i))));
            dataTable.addCell(new Cell().add(new Paragraph("Item " + i)));
            dataTable.addCell(new Cell().add(new Paragraph("Design " + i)));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i * 2))));
            dataTable.addCell(new Cell().add(new Paragraph(String.valueOf(i * 1.5))));
        }

        document.add(dataTable);

        // Bank Details Footer
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{1}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.addCell(new Cell().add(new Paragraph("CASH:").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("BANK: ABC Bank").setBold()).setBorder(Border.NO_BORDER));
        footerTable.addCell(new Cell().add(new Paragraph("RTGS DETAILS: 12345678").setBold()).setBorder(Border.NO_BORDER));

        // Add Footer Content
        footerTable.addCell(new Cell().add(new Paragraph("COMPANY INFO: ABC Pvt Ltd, Contact: 9876543210, Email: info@company.com"))
                .setTextAlignment(TextAlignment.CENTER).setBorder(Border.NO_BORDER));
        document.add(footerTable);

        // Add Header
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

        String formattedDate = convertTimestampToDate(tdate);
        document.close();

        // Open the PDF after saving
        openPdf1(dest);

    }


}
