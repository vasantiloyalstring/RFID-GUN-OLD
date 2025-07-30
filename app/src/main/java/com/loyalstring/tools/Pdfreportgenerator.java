package com.loyalstring.tools;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.loyalstring.Activities.Billlistactivity;
import com.loyalstring.modelclasses.Itemmodel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Pdfreportgenerator {

    //    private WeakReference<Context> contextRef;
    Context context;

    //
    public Pdfreportgenerator(Context context) {
        this.context = context;
    }

    public void generatereportpdf(Context context, Map<String, List<Itemmodel>> skureport1, String reporttype, int i) throws FileNotFoundException, MalformedURLException {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            if(reporttype.equalsIgnoreCase("skureport")){
                if (i == 1) {
                    new SavePdfTask().execute(skureport1);
                } else if (i == 2) {
                    new SaveAllPdfTask().execute(skureport1);
                }
            }else if(reporttype.equalsIgnoreCase("salereport")){
                if (i == 1) {
                    new SavesalePdfTask().execute(skureport1);
                }
            }

        }
    }
    private class SavesalePdfTask extends AsyncTask<Map<String, List<Itemmodel>>, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(context);
        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Generating PDF...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Map<String, List<Itemmodel>>... treeMaps) {
            Map<String, List<Itemmodel>> skureport1 = treeMaps[0];

            try {
                File pdfDir = new File(context.getExternalFilesDir(null), "PDFs");
                if (!pdfDir.exists()) pdfDir.mkdirs();

                file = new File(pdfDir, "skureport1.pdf");

                PdfWriter writer = new PdfWriter(file);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                for (Map.Entry<String, List<Itemmodel>> entry : skureport1.entrySet()) {
                    List<Itemmodel> items = entry.getValue();
                    String itemcode = entry.getKey();
                    double gwt = 0, swt = 0, nwt = 0;
                    int qty = 0;

                    Paragraph header = new Paragraph("Sale Report")
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontSize(16);
                    document.add(header);

                    for (Itemmodel m : items) {
                        gwt += m.getGrossWt();
                        swt += m.getStoneWt();
                        nwt += m.getNetWt();
                        qty++;
                    }

                    Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

                    Paragraph left = new Paragraph("ItemCode: " + itemcode + "\nOrder No: N/A\nTotal Items: " + qty)
                            .setFontSize(12);

                    Paragraph right = new Paragraph("G wt: " + gwt + "\nS wt: " + swt + "\nN wt: " + nwt)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setFontSize(12);

                    table.addCell(left);
                    table.addCell(right);
                    document.add(table);

                    // Add image if available
                    String imageUrl = items.get(0).getItemCode() + ".jpg";
                    File imageFile = new File(context.getExternalFilesDir("images"), imageUrl);

                    if (imageFile.exists()) {
                        ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                        Image image = new Image(imageData);
                        image.setWidth(400);
                        image.setHeight(500);
                        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(image);
                    }

                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }

                document.close();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if (result) {
                openPdf(file);
            } else {
                Toast.makeText(context, "PDF generation failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void openPdf(File pdfFile) {
            Uri uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".provider",
                    pdfFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

    /*  private class SavesalePdfTask extends AsyncTask<Map<String, List<Itemmodel>>, Void, Boolean> {
        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Generating PDFs...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(true); // Makes progress dialog more appropriate for long tasks
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Map<String, List<Itemmodel>>... treeMaps) {
            Map<String, List<Itemmodel>> skureport1 = treeMaps[0];
            String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/skureport1.pdf";

            try {
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                for (Map.Entry<String, List<Itemmodel>> entry : skureport1.entrySet()) {


                    List<Itemmodel> items = entry.getValue();
                    String itemcode = entry.getKey();
                    double gwt = 0;
                    double swt = 0;
                    double nwt = 0;
                    int qty = 0;

                    // Add header for the current page
                    Paragraph header = new Paragraph("sale Report ")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(18);
                    document.add(header);

                    for (Itemmodel m : items) {
                        gwt += m.getGrossWt();
                        swt += m.getStoneWt();
                        nwt += m.getNetWt();
                        qty++;
                    }

                    Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

// First cell: Right-aligned text
                    Paragraph details1 = new Paragraph("G wt  : " + items.get(0).getGrossWt() + "\nS wt  : " + items.get(0).getStoneWt()
                            + "\nN Wt  : " + items.get(0).getNetWt()
                            + "\nTotal Wt  : " + nwt)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setFontSize(14);



// Second cell: Left-aligned text
                    Paragraph details = new Paragraph("ItemCode  : " + itemcode + "\nOrder No  : "
                            + "\nTotal Items  : " + qty)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(14);

                    table.addCell(details);
                    table.addCell(details1);
                    document.add(table);
                    // Add image if available
                    String imageUrl = items.get(0).getItemCode() + ".jpg";
                    File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

                    if (checkIfFileExists(imageFile)) {
                        ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                        Image image = new Image(imageData);
                        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        image.setWidth(400);
                        image.setHeight(500);
//                        Image image = resizeImage(imageFile); // Use the resizeImage method
//                        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(image);
//                        document.add(image);
                    }

                    // Add page break after each item
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }

                // Close the document
                document.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();
            if (result) {
                // Open the PDF if the operation was successful
                openPdf1(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/skureport.pdf");
            } else {
                // Show error message if the operation failed
                Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
            }
        }


    }
*/
    public void generatereportpdf1(Billlistactivity billlistactivity, HashMap<String, List<Itemmodel>> billmap, int i) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            TreeMap<String, List<Itemmodel>> skureport1 = new TreeMap<>();
            skureport1.putAll(billmap);

                new SaveAllPdfTask().execute(skureport1);

        }

    }

    private class SaveAllPdfTask extends AsyncTask<Map<String, List<Itemmodel>>, Void, String> {

        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Generating PDFs...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.setIndeterminate(true); // Makes progress dialog more appropriate for long tasks
            dialog.show();
        }

        @Override
        protected String doInBackground(Map<String, List<Itemmodel>>... params) {
            Map<String, List<Itemmodel>> skureport1 = params[0];

            Log.e("PDFGEN", "Starting PDF generation. Number of entries: " + skureport1.size());

            for (Map.Entry<String, List<Itemmodel>> entry : skureport1.entrySet()) {
                String key = entry.getKey();
                List<Itemmodel> items = entry.getValue();

                String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        .toString() + File.separator + "Loyalstring files" + File.separator + "Pdfs" + File.separator + items.get(0).getCustomerName() + ".pdf";

                try {
                    Log.e("PDFGEN", "Generating PDF for: " + items.get(0).getCustomerName());

                    PdfWriter writer = new PdfWriter(pdfPath);
                    PdfDocument pdfDoc = new PdfDocument(writer);
                    Document document = new Document(pdfDoc, PageSize.A4);


                    Paragraph header = new Paragraph("Bill Report ")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(18);
                    document.add(header);


                    for (int i = 0; i < items.size(); i++) {
                        Itemmodel m = items.get(i);
//                        addItemDetailsToPdf(document, m);

                        Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

// First cell: Right-aligned text
                        Paragraph details1 = new Paragraph("G wt  : " + m.getGrossWt() + "\nS wt  : " + m.getStoneWt()
                                + "\nN Wt  : " + m.getNetWt())
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setFontSize(14);



// Second cell: Left-aligned text
                        Paragraph details = new Paragraph("Customer Name  : " + m.getCustomerName() + "\nOrder No  : "+m.getInvoiceNumber()
                                + "\nItemcode  : " + m.getItemCode()+"\nNotes  : "+m.getPartyCode())
                                .setTextAlignment(TextAlignment.LEFT)
                                .setFontSize(14);

                        table.addCell(details);
                        table.addCell(details1);
                        document.add(table);


                        String imageUrlString = m.getImageUrl(); // e.g., "img1.jpg,img2.jpg,img3.jpg"
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
                        File imageFile = new File(onlineimage);

                        if (checkIfFileExists(imageFile)) {
                            ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                            Image image = new Image(imageData);
                            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            image.setWidth(400);
                            image.setHeight(500);
//                            Image image = resizeImage(imageFile); // Use the resizeImage method
//                            image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                            document.add(image);
                        }

                        if (i + 1 < items.size()) {
                            document.add(new AreaBreak());
                        }
                    }

                    document.close();
                    Log.e("PDFGEN", "PDF generated successfully: " + pdfPath);

                } catch (Exception e) {
                    Log.e("PDFGEN", "Error generating PDF: " + e.getMessage(), e);
                    return "Something went wrong: " + e.getMessage();
                }
            }

            Log.e("PDFGEN", "PDF generation complete. Total entries: " + skureport1.size());
            return "All PDFs generated";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            Log.e("PDFGEN", "Task completed: " + result);
            Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
        }
    }

    private void addItemDetailsToPdf(Document document, Itemmodel m) {
        try {
            Paragraph details = new Paragraph("ITEM NAME  \n" + m.getProduct())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(details);

            Paragraph details1 = new Paragraph("ITEM Code  \n" + m.getItemCode())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(details1);

            Paragraph tgwt = new Paragraph("G Wt  :" + m.getGrossWt() + "  S Wt  :" + m.getStoneWt() + "  N Wt  :" + m.getNetWt() + "  S Amount  :" + m.getStoneAmount())
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(tgwt);

            String note = m.getPartyCode() != null && m.getPartyCode().startsWith("https://jjj.panel") ?
                    "image found" : m.getPartyCode();
            Paragraph noteParagraph = new Paragraph("Note  :" + note)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(noteParagraph);

//            Paragraph finewt = new Paragraph("fine+wast  :" + (m.getFixedWastage() + m.getMakingPer()))
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFontSize(18);
//            document.add(finewt);
//
//            Paragraph total = new Paragraph("total%  :" + (m.getNetWt() * (m.getFixedWastage() + m.getMakingPer())) / 100)
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFontSize(18);
//            document.add(total);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SavePdfTask extends AsyncTask<Map<String, List<Itemmodel>>, Void, Boolean> {

        ProgressDialog progressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.show();

        }

        @Override
        protected Boolean doInBackground(Map<String, List<Itemmodel>>... params) {
            Map<String, List<Itemmodel>> skureport1 = params[0];
            String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/skureport1.pdf";

            try {
                PdfWriter writer = new PdfWriter(dest);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc, PageSize.A4);

                for (Map.Entry<String, List<Itemmodel>> entry : skureport1.entrySet()) {
                    List<Itemmodel> items = entry.getValue();
                    String sku = entry.getKey();
                    double gwt = 0;
                    double swt = 0;
                    double nwt = 0;
                    int qty = 0;

                    // Add header for the current page
                    Paragraph header = new Paragraph("Sku Report ")
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(18);
                    document.add(header);

                    for (Itemmodel m : items) {
                        gwt += m.getGrossWt();
                        swt += m.getStoneWt();
                        nwt += m.getNetWt();
                        qty++;
                    }

                    Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();

// First cell: Right-aligned text
                    Paragraph details1 = new Paragraph("G wt  : " + items.get(0).getGrossWt() + "\nS wt  : " + items.get(0).getStoneWt()
                            + "\nN Wt  : " + items.get(0).getNetWt()
                            + "\nTotal Wt  : " + nwt)
                            .setTextAlignment(TextAlignment.RIGHT)
                            .setFontSize(14);



// Second cell: Left-aligned text
                    Paragraph details = new Paragraph("Sku  : " + items.get(0).getStockKeepingUnit() + "\nOrder No  : "
                            + "\nTotal Items  : " + qty)
                            .setTextAlignment(TextAlignment.LEFT)
                            .setFontSize(14);

                    table.addCell(details);
                    table.addCell(details1);
                    document.add(table);
                    // Add image if available
                    String imageUrl = items.get(0).getItemCode() + ".jpg";
                    File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

                    if (checkIfFileExists(imageFile)) {
                        ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                        Image image = new Image(imageData);
                        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        image.setWidth(400);
                        image.setHeight(500);
//                        Image image = resizeImage(imageFile); // Use the resizeImage method
//                        image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                        document.add(image);
//                        document.add(image);
                    }

                    // Add page break after each item
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }

                // Close the document
                document.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss();
            if (result) {
                // Open the PDF if the operation was successful
                openPdf1(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/skureport.pdf");
            } else {
                // Show error message if the operation failed
                Toast.makeText(context, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private Image resizeImage(File imageFile) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Define the target size for the image
        int targetWidth = 400; // Adjust as needed
        int targetHeight = 500; // Adjust as needed

        // Resize the image if necessary
        if (width > targetWidth || height > targetHeight) {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Compress the image
            byte[] imageBytes = baos.toByteArray();

            ImageData imageData = ImageDataFactory.create(imageBytes);
            return new Image(imageData);
        }

        ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
        return new Image(imageData);
    }


    private void savePdfToDownloadFolder(TreeMap<String, List<Itemmodel>> skureport1) throws FileNotFoundException, MalformedURLException {


        String dest = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/skureport.pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc, PageSize.A4);

        for (Map.Entry<String, List<Itemmodel>> entry : skureport1.entrySet()) {
            List<Itemmodel> items = entry.getValue();
            String sku = entry.getKey();
            double gwt = 0;
            double swt = 0;
            double nwt = 0;
            int qty = 0;


            // Add header for the current page
            Paragraph header = new Paragraph("Sku Report ")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(18);
            document.add(header);

            for (Itemmodel m : items) {
                gwt += m.getGrossWt();
                swt += m.getStoneWt();
                nwt += m.getNetWt();
                qty++;


            }

            Paragraph details1 = new Paragraph("G wt  : " + items.get(0).getGrossWt() + "\nS wt  : " + items.get(0).getStoneWt()
                    + "\nN Wt  : " + items.get(0).getNetWt()
                    + "\nTotal Wt  : " + nwt
//                    items.get(0).getInvoiceNumber() + "\nTotal Grosswt  : " + gwt + "\nTotal Stonewt  : " +
//                    swt + "\nTotal Netwt  : " + nwt + "\nTotal Items  : " + qty
            )
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14);
            document.add(details1);

            // Add details for the current page
//            Paragraph details = new Paragraph("Sku  : " + items.get(0).getStockKeepingUnit() +"\nInvoice No  : " + items.get(0).getInvoiceNumber() + "\nTotal Grosswt  : " + gwt + "\nTotal Stonewt  : " + swt + "\nTotal Netwt  : " + nwt + "\nTotal Items  : " + qty)
//                    .setTextAlignment(TextAlignment.LEFT)
//                    .setFontSize(14);
//            document.add(details);


            Paragraph details = new Paragraph("Sku  : " + items.get(0).getStockKeepingUnit() + "\nOrder No  : "
                    + "\nTotal Items  : " + qty
//                    items.get(0).getInvoiceNumber() + "\nTotal Grosswt  : " + gwt + "\nTotal Stonewt  : " +
//                    swt + "\nTotal Netwt  : " + nwt + "\nTotal Items  : " + qty
            )
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(14);
            document.add(details);


            // Add image if available
            String imageUrl = items.get(0).getItemCode() + ".jpg";
            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Loyalstring files/images/" + imageUrl);

            if (checkIfFileExists(imageFile)) {
                ImageData imageData = ImageDataFactory.create(imageFile.getAbsolutePath());
                Image image = new Image(imageData);
                image.setHorizontalAlignment(HorizontalAlignment.CENTER);
                image.setWidth(400);
                image.setHeight(500);
                document.add(image);
            }

            // Add page break after each item
            document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
        }

        // Close the document
        document.close();

        // Open the PDF
        openPdf1(dest);
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
}
