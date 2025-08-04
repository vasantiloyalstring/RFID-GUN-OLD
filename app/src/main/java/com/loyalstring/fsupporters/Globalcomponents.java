package com.loyalstring.fsupporters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.loyalstring.R;
import com.loyalstring.database.StorageClass;
import com.loyalstring.database.product.EntryDatabase;
import com.loyalstring.database.support.Valuesdb;
import com.loyalstring.modelclasses.Ratemodel;
import com.rscja.barcode.BarcodeDecoder;
import com.rscja.deviceapi.RFIDWithUHFUART;
import com.rscja.deviceapi.entity.BarcodeEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class Globalcomponents {


    public boolean checkpower(Context activity, RFIDWithUHFUART mReader, int power, TextView toolpower) {
        if (mReader == null) {
            Log.d("checkpower", "p1 "+"reader null");
            return false;
        }
        if (mReader.getPower() == power) {
            toolpower.setText(String.valueOf(power));
            Log.d("checkpower", "p2 "+"reader null  "+mReader+"  "+power+"  "+toolpower);
            return true;
        } else {
            if(mReader.setPower(30)){
                toolpower.setText(String.valueOf(power));
                Log.d("checkpower", "p3 "+"reader null  "+mReader+"  "+power+"  "+toolpower);
                return true;
            }else{
                Log.d("checkpower", "p4 "+"reader null  "+mReader+"  "+power+"  "+toolpower);
                return false;
            }
        }
    }

    public void keepScreenOn(boolean keepOn, FragmentActivity activity) {
        if (activity != null) {
            if (keepOn) {
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } else {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }


    public void barcodescan(BarcodeDecoder barcodeDecoder, EditText text){
        barcodeDecoder.startScan();
        barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
            @Override
            public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getResultCode());
                if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
                    text.setText(barcodeEntity.getBarcodeData());
                    Log.e("TAG", "data==========================:" + barcodeEntity.getBarcodeData());
                } else {
                    text.setText("");
                }
            }
        });

    }

    public void showbarcode(Context activity, TextView sbarno, String title, BarcodeDecoder barcodeDecoder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout, null);
        builder.setView(dialogView);
        EditText editText = dialogView.findViewById(R.id.editText);
        TextView textView = dialogView.findViewById(R.id.dialogtext);
        ImageView scanner = dialogView.findViewById(R.id.bscanner);
        if (title.matches("itemcode")) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        }
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcodeDecoder.startScan();
            }
        });

        barcodeDecoder.setDecodeCallback(new BarcodeDecoder.DecodeCallback() {
            @Override
            public void onDecodeComplete(BarcodeEntity barcodeEntity) {
                Log.e("TAG", "BarcodeDecoder==========================:" + barcodeEntity.getResultCode());
                if (barcodeEntity.getResultCode() == BarcodeDecoder.DECODE_SUCCESS) {
                    editText.setText(barcodeEntity.getBarcodeData());
                    Log.e("TAG", "data==========================:" + barcodeEntity.getBarcodeData());
                } else {
                    editText.setText("");
                }
            }
        });

        if (title.matches("barcode")) {
            textView.setText("Enter Barcode No");
            barcodeDecoder.startScan();
        } else {
            textView.setText("Enter Itemcode No");
            scanner.setVisibility(View.GONE);
        }
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (editText.getText().toString().trim().matches("")) {
                    Toast.makeText(activity, "Enter value", Toast.LENGTH_SHORT).show();
//                    globaltoast(activity, "Please enter or scan bar code", "", "");
//                    return;
                }
//                if (tit.matches("barcode")) {
//                    barcodetext.setText(editText.getText().toString().trim());
//                } else {
                sbarno.setText(editText.getText().toString().trim());
//                }

            }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);

    }

    public void getlist(String title, TextView t, FragmentActivity activity) {

        List<String> bottomlist = new ArrayList<>();
        Valuesdb db = new Valuesdb(activity);
        if (title.equalsIgnoreCase("category")) {
            Log.d("@@ title2"," @@ title2"+title);
            bottomlist = db.getcatpro();
        }
        if (bottomlist.isEmpty()) {
            Toast.makeText(activity, "no data found", Toast.LENGTH_SHORT).show();
//            return;
        }

        showbottom(activity, title, t, bottomlist);
        Log.d("@@ tit","@@ titale"+title);
    }

    public void showbottom(Context activity, String title, TextView t, List<String> bottomlist) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(activity);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View contentView = inflater.inflate(R.layout.bottom_sheet_layout, null);
//        View contentView = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(contentView);
        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView ttitle = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        TextView purehint = contentView.findViewById(R.id.purehint);

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        addbtn.setVisibility(View.GONE);
        ttitle.setText(title);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemname.getText().toString().isEmpty()) {
//                    globaltoast(activity, "Please enter value", "", "");
                    return;
                }
                for (String m : bottomlist) {
                    if (m.equalsIgnoreCase(itemname.getText().toString())) {
//                        globaltoast(activity, "value already exist", "", "");
//                        break;
                        return;
                    }
                }

                Valuesdb db = new Valuesdb(activity);
                if (title.equalsIgnoreCase("box")) {
                    Log.d("@@ tit","@@ titale"+title);
                    long ad = db.addbox(itemname.getText().toString().trim(), activity);
                    if (ad != -1) {
                        // Insertion successful
//                        globaltoast(activity, "box added succesfully", "", "");
                        List<String> updatedList = new ArrayList<>(bottomlist);
                        updatedList.clear();
                        updatedList.addAll(db.getboxes());
                        bottomlist.clear();
                        bottomlist.addAll(updatedList);
                        itemname.setText("");
                    } else {
                        // Category already exists
//                        globaltoast(activity, "box already exist to failed to add category", "", "");
                        itemname.setText("");
                    }

                } if (title.equalsIgnoreCase("counter")) {
                    Log.d("@@ tit","@@ titale"+title);
                    long ad = db.addCounter(itemname.getText().toString().trim(), activity);
                    if (ad != -1) {
                        // Insertion successful
//                        globaltoast(activity, "box added succesfully", "", "");
                        List<String> updatedList = new ArrayList<>(bottomlist);
                        updatedList.clear();
                        updatedList.addAll(db.getCounters());
                        bottomlist.clear();
                        bottomlist.addAll(updatedList);
                        itemname.setText("");
                    } else {
                        // Category already exists
//                        globaltoast(activity, "box already exist to failed to add category", "", "");
                        itemname.setText("");
                    }

                }

                else {
                    for (String m : bottomlist) {
                        if (m.equalsIgnoreCase(itemname.getText().toString())) {
//                            globaltoast(activity, "category already exist", "", "");

                            return;

                        }
                    }
                    long ad = db.addcategory(itemname.getText().toString().trim(), activity);
                    if (ad != -1) {
                        // Insertion successful
//                        globaltoast(activity, "category added succesfully", "", "");
                        List<String> updatedList = new ArrayList<>(bottomlist);
                        updatedList.clear();
                        Log.d("@@ title1"," @@ title1"+title);
                        updatedList.addAll(db.getcatpro());
                        bottomlist.clear();
                        bottomlist.addAll(updatedList);
                        itemname.setText("");
                        adapter.notifyDataSetChanged();
                    } else {
                        // Category already exists
//                        globaltoast(activity, "category already exist to failed to add category", "", "");
                        itemname.setText("");
                    }
                }
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(itemname.getWindowToken(), 0);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selecteditem = (String) adapterView.getItemAtPosition(i);
                t.setText(selecteditem);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();

    }


    public void getprolist(String cattext, TextView protext, Context activity) {
        List<String> bottomlist = new ArrayList<>();
        Valuesdb db = new Valuesdb(activity);
        bottomlist = db.getProductsByCategory(cattext);
        if (bottomlist.isEmpty()) {
//            globaltoast(activity, "no data found", "", "");
//            return;
        }
        showprobottom(activity, "Product", protext, bottomlist, cattext);

    }

    public void showprobottom(Context activity, String title, TextView t, List<String> bottomlist, String cattext) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(activity);

        View contentView = inflater.inflate(R.layout.bottom_sheet_layout, null);

//        View contentView = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(contentView);
        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView ttitle = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText proitem = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        TextView purehint = contentView.findViewById(R.id.purehint);

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ttitle.setText(title);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (proitem.getText().toString().isEmpty()) {
//                    globaltoast(activity, "Please enter value", "", "");
                    return;
                }
                for (String m : bottomlist) {
                    if (m.equalsIgnoreCase(proitem.getText().toString())) {
//                        globaltoast(activity, "value already exist", "", "");
                        return;
                    }
                }

                Valuesdb db = new Valuesdb(activity);
                boolean ad = db.addproduct(proitem.getText().toString().trim(), activity, cattext);
                if (ad) {
                    // Insertion successful
//                    globaltoast(activity, "product added succesfully", "", "");
                    List<String> updatedList = new ArrayList<>(bottomlist);
                    updatedList.clear();
                    updatedList.addAll(db.getProductsByCategory(cattext));
                    bottomlist.clear();
                    bottomlist.addAll(updatedList);
                    adapter.notifyDataSetChanged();
                    proitem.setText("");
                } else {
                    // Category already exists
//                    globaltoast(activity, "value already exist to failed to add category", "", "");
                    proitem.setText("");
                }

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(proitem.getWindowToken(), 0);

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selecteditem = (String) adapterView.getItemAtPosition(i);
                t.setText(selecteditem);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }


    public void getpurity(String cat, TextView edpuritytext, Context activity) {
        List<String> purity = new ArrayList<>();
//        iddatabase db = new iddatabase(activity);

        Valuesdb db = new Valuesdb(activity);
        List<Ratemodel> rl = db.getgoldratelist();
        for (Ratemodel r : rl) {
            if (r.getCategory().equalsIgnoreCase(cat)) {
                purity.add(r.getPurity());
            }
        }

        showpuritybottom(activity, "Purity", edpuritytext, purity, cat);
    }
    public void showpuritybottom(Context activity, String title, TextView t, List<String> bottomlist, String cat) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(activity);

        View contentView = inflater.inflate(R.layout.bottom_sheet_layout, null);
//        View contentView = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(contentView);
        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView ttitle = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);
        TextView purehint = contentView.findViewById(R.id.purehint);

        bottomSheetDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ttitle.setText(title);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemname.getText().toString().isEmpty()) {
//                    globaltoast(activity, "Please enter purity", "", "");
                    return;
                }
                for (String m : bottomlist) {
                    if (m.equalsIgnoreCase(itemname.getText().toString())) {
//                        globaltoast(activity, "category already exist", "", "");
                        return;
                    }
                }
//                iddatabase db = new iddatabase(activity);
//                boolean ad = db.addpurity(cat, itemname.getText().toString().trim(), "", activity);

                boolean ad = updaterate(cat, itemname.getText().toString().trim(), 0, activity);
                if (ad) {
                    // Insertion successful
                    bottomlist.clear();
                    Valuesdb db = new Valuesdb(activity);
                    List<Ratemodel> rl = db.getgoldratelist();
                    for (Ratemodel r : rl) {
                        if (r.getCategory().equalsIgnoreCase(cat)) {

                            bottomlist.add(r.getPurity());
                        }
                    }

//                    globaltoast(activity, "added succesfully", "", "");
//                    List<String> updatedList = new ArrayList<>(bottomlist);
//                    updatedList.clear();
//                    updatedList.addAll(db.getpurity("p", cat));
//                    bottomlist.clear();
//                    bottomlist.addAll(updatedList);
                    itemname.setText("");
                    adapter.notifyDataSetChanged();
                } else {
                    // Category already exists
//                    globaltoast(activity, "purity already exist to failed to add category", "", "");
                    itemname.setText("");
                }

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });
        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selecteditem = (String) adapterView.getItemAtPosition(i);
                t.setText(selecteditem);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }
    public boolean updaterate(String category, String purity, double rate, Context activity) {
        Valuesdb db = new Valuesdb(activity);
        return db.updaterate(category, purity, rate);
    }
    public void getboxes(Context activity, TextView boxtext) {
        List<String> bottomlist = new ArrayList<>();
        Valuesdb db = new Valuesdb(activity);
        bottomlist = db.getboxes();
        if (bottomlist.isEmpty()) {
//            globaltoast(activity, "no data found", "", "");
//            return;
        }

        showbottom(activity, "Box", boxtext, bottomlist);
        Log.d("@@ tit","@@ titale"+boxtext);
    }

    public void changepowerg(Activity context, String tit, StorageClass storageClass, TextView toolbartext2, RFIDWithUHFUART mReader) {

        if (mReader != null) {
            if (mReader.isInventorying()) {
                Toast.makeText(context, "Please stop scanner to change power", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        ArrayList<String> bottomlist = new ArrayList<>();
//        bottomlist.clear();
        for (int i = 1; i <= 30; i++) {
            bottomlist.add(String.valueOf(i));
        }
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        bottomSheetDialog.setCancelable(false);

        bottomSheetDialog.setCancelable(false);

        View contentView = context.getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);

        bottomSheetDialog.setContentView(contentView);

        ImageButton close = contentView.findViewById(R.id.closeButton);
        TextView title = contentView.findViewById(R.id.maintitle);
        Button addbtn = contentView.findViewById(R.id.additem);
        EditText itemname = contentView.findViewById(R.id.itemname);
        ListView spinnerlist = contentView.findViewById(R.id.spinnerlist);

        addbtn.setVisibility(View.GONE);
        itemname.setVisibility(View.GONE);
        title.setText(String.format("%s power", tit));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, bottomlist);
        spinnerlist.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        spinnerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selecteditem = (String) adapterView.getItemAtPosition(position);
                String set = "";

                if (tit.equalsIgnoreCase("product")) {
                    storageClass.setppower(selecteditem);
                    set = storageClass.getppower();
                }
                if (tit.equalsIgnoreCase("inventory")) {
                    storageClass.setipower(selecteditem);
                    set = storageClass.getipower();
                }
                if (tit.equalsIgnoreCase("search")) {
                    storageClass.setspower(selecteditem);
                    set = storageClass.getspower();
                }
                if (tit.equalsIgnoreCase("transaction")) {
                    storageClass.settpower(selecteditem);
                    set = storageClass.gettpower();
                }
                if (tit.equalsIgnoreCase("stock transfer")) {
                    storageClass.setstpower(selecteditem);
                    set = storageClass.getstpower();
                }
                if (tit.equalsIgnoreCase("stock history")) {
                    storageClass.setshpower(selecteditem);
                    set = storageClass.getshpower();
                }
                if (tit.equalsIgnoreCase("stocktransfer")) {
                    storageClass.setstapower(selecteditem);
                    set = storageClass.getstapower();
                }
                if (selecteditem.equals(set)) {
                    toolbartext2.setText(set);
                    if (mReader != null) {
                        mReader.setPower(Integer.parseInt(set));
                    }
                } else {
                    Toast.makeText(context, "failed to set power please try again", Toast.LENGTH_SHORT).show();
                }

                Log.d("global", "powerss  " + storageClass.getppower() + " " + storageClass.getipower() + "  " + storageClass.getspower());

                bottomSheetDialog.dismiss();


            }
        });
        //storageClass.getireadpower()


        bottomSheetDialog.show();


    }

    public boolean checkfileexist(String frag) {

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");
        File productDir = new File(dir, frag);
        if (!productDir.exists()) {
            if (!productDir.mkdirs()) {
                Log.e("TAG", "Failed to create directory: " + productDir.getAbsolutePath());
                return false;
            }
        }
        return true;

    }

    public boolean createFolders(ArrayList<String> folders) {
        File rootDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Loyalstring files");

        // Check if the root directory exists, and create it if it doesn't
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                Log.e("TAG", "Failed to create root directory: " + rootDir.getAbsolutePath());
                return false;
            }
        }

        // Iterate through each folder name and create the corresponding directory
        for (String folder : folders) {
            File folderDir = new File(rootDir, folder);
            if (!folderDir.exists()) {
                if (!folderDir.mkdirs()) {
                    Log.e("TAG", "Failed to create directory: " + folderDir.getAbsolutePath());
                    return false;
                }
            }
        }

        // All folders were created successfully
        return true;
    }


    //emails
    public List<String> readallemails(Context context) {
        List<String> itemlist = new ArrayList<>();
        Valuesdb db = new Valuesdb(context);
        itemlist = db.reademails(context);
        return itemlist;
    }

    public static class sendglobalemil extends AsyncTask<Void, Void, Boolean> {
        private String sendemail, sendpass;

        List<String> recemail;
        private String body;
        private String type;
        private Map<String, String> link;
        private String subject;
        Context context;
        private ProgressDialog dialog;

        public sendglobalemil(String sendemail, String sendpass, List<String> recemail, String body, String type, Map<String, String> link, String subject, Context context) {
            this.sendemail = sendemail;
            this.sendpass = sendpass;
            this.recemail = recemail;
            this.body = body;
            this.type = type;
            this.link = link;
            this.subject = subject;
            this.context = context;
            dialog = new ProgressDialog(context);
            dialog.setMessage("Sending email please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {

                // Set your SMTP server properties
                /*Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.hostinger.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.auth", "true");*/

                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.hostinger.com");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.ssl.enable", "true");  // Disable SSL
                props.put("mail.smtp.starttls.enable", "false");  // Enable TLS
                props.put("mail.smtp.auth", "true");

                // Create a Session with authentication
                Session session = Session.getInstance(props, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sendemail, sendpass);
                    }
                });


                for (String s : recemail) {
                    Log.d("check emails ", "  " + sendemail + "  " + sendpass + "  " + s + "  " + subject + "  " + body);
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(sendemail));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(s));
                    message.setSubject(subject);
                    MimeMultipart multipart = new MimeMultipart();

                    // Create a MimeBodyPart for the email body
                    MimeBodyPart textPart = new MimeBodyPart();
                    if (type.isEmpty()) {
                        textPart.setText(body);
                    } else {
                        textPart.setContent(body, "text/html");
                    }
                    multipart.addBodyPart(textPart);

//                    message.setContent(multipart);
                    if (!link.isEmpty()) {
                        for (Map.Entry<String, String> entry : link.entrySet()) {
                            String filename = entry.getKey();
                            String filepath = entry.getValue();

                            // Create a MimeBodyPart for each attached file
                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            DataSource source = new FileDataSource(filepath);
                            attachmentPart.setDataHandler(new DataHandler(source));
                            attachmentPart.setFileName(filename); // Set the desired filename
                            multipart.addBodyPart(attachmentPart);
                        }
                        message.setContent(multipart);
                    }


                    Transport.send(message);
                }
                /*if(!link.isEmpty()){
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(sendemail));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailid));
                    message.setSubject(subject);
                    MimeMultipart multipart = new MimeMultipart();

                    // Create a MimeBodyPart for the email body
                    MimeBodyPart textPart = new MimeBodyPart();
                    if (type.isEmpty()) {
                        textPart.setText(body);
                    } else {
                        textPart.setContent(body, "text/html");
                    }
                    multipart.addBodyPart(textPart);

                    for (Map.Entry<String, String> entry : link.entrySet()) {
                            String filename = entry.getKey();
                            String filepath = entry.getValue();

                            // Create a MimeBodyPart for each attached file
                            MimeBodyPart attachmentPart = new MimeBodyPart();
                            DataSource source = new FileDataSource(filepath);
                            attachmentPart.setDataHandler(new DataHandler(source));
                            attachmentPart.setFileName(filename); // Set the desired filename
                            multipart.addBodyPart(attachmentPart);
                        }
                    message.setContent(multipart);


                    Transport.send(message);



                }*/


                // Create a new MimeMessage
                /*for (String mailid : recemail) {
//                    MimeMessage message = new MimeMessage(session);
//                    message.setFrom(new InternetAddress(sendemail));
//                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailid));
//                    message.setSubject(subject);
                *//*if(type.isEmpty()) {
                    message.setText(body);
                }else{
                    message.setContent(body, "text/html");
                }
                if(!link.isEmpty()){

                }*//*

                    MimeMultipart multipart = new MimeMultipart();

                    // Create a MimeBodyPart for the email body
                    MimeBodyPart textPart = new MimeBodyPart();
                    if (type.isEmpty()) {
                        textPart.setText(body);
                    } else {
                        textPart.setContent(body, "text/html");
                    }
                    multipart.addBodyPart(textPart);

                    // Check if there's a file to attach
//                    if (!link.isEmpty()) {
//                        for (Map.Entry<String, String> entry : link.entrySet()) {
//                            String filename = entry.getKey();
//                            String filepath = entry.getValue();
//
//                            // Create a MimeBodyPart for each attached file
//                            MimeBodyPart attachmentPart = new MimeBodyPart();
//                            DataSource source = new FileDataSource(filepath);
//                            attachmentPart.setDataHandler(new DataHandler(source));
//                            attachmentPart.setFileName(filename); // Set the desired filename
//                            multipart.addBodyPart(attachmentPart);
//                        }
//                    }
//                    message.setContent(multipart);
//
//
//                    Transport.send(message);

                    // Email sent successfully

                }*/
                return true;

            } catch (MessagingException e) {

                Log.e("check emails", "Error sending email: " + e.getMessage());
                Log.e("EmailError", "Error sending email", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Email sent successfully
                Log.d("sentsuccess", "okay");
                dialog.dismiss();
                Toast.makeText(context, "email sent successfully", Toast.LENGTH_SHORT).show();
                // You can add code here to handle success, e.g., show a success message
            } else {
                // Email sending failed
                dialog.dismiss();
                Log.d("sent failed", "okay");
                Toast.makeText(context, "failed to send email", Toast.LENGTH_SHORT).show();

                // You can add code here to handle the failure, e.g., show an error message
            }
        }
    }

    public boolean insetemail(String newEmail, Context context) {
        EntryDatabase db = new EntryDatabase(context);
        return db.insertEmail(newEmail);
    }


}
