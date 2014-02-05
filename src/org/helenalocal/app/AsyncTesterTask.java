package org.helenalocal.app;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.helenalocal.base.*;
import org.helenalocal.base.get.BuyerHub;
import org.helenalocal.base.get.OrderHub;
import org.helenalocal.base.get.ProducerHub;
import org.helenalocal.base.post.GrowerHub;

import java.util.ArrayList;
import java.util.Calendar;

public class AsyncTesterTask extends AsyncTask<Void, Void, Intent> {

    private static final String Tag = "AsyncTesterTask";
    private Context _context;

    public AsyncTesterTask(Context context) {
        _context = context;
    }

    @Override
    protected Intent doInBackground(Void... params) {

        Intent email = null;

        //TODO shane -- This is a working example... Let me know if you have questions about it... :)
        // public Producer(String PID, String name, String contactEmail, String websiteUrl, String photoUrl, String location) {
        Producer producer = new Producer("P-2013-0","Western Montana Growers’ Cooperative","grower@wmgcoop.com","http://www.wmgcoop.com/","http://g.virbcdn.com/_f2/images/58/PageImage-524372-4680215-WMGC_WebBanner.jpg","Arlee, MT 59821");

        // public Item(String IID,Producer producer,boolean inCsaThisWeek, String category, String productDesc, String productUrl, String productImageUrl, Integer unitsAvailable,
        //        String unitDesc, Double unitPrice, Calendar deliveryDate, String note) {
        Item item = new Item("I-2014-2-2-3","P-2013-0", true, "Produce", "Leeks","http://en.wikipedia.org/wiki/Leek‎",
                "http://upload.wikimedia.org/wikipedia/commons/thumb/4/43/Leek.jpg/160px-Leek.jpg",20,"10 lbs",10.01, Calendar.getInstance(),"note 1");

        // growerAgreementId only needed from UI submit... Not part of the item object.
        String growerAgreementId = "N/A";
        GrowerHub growerHub = new GrowerHub();
        try {
            // test producerhub...
            ProducerHub producerHub = new ProducerHub();
            ArrayList<Producer> producerArrayList = new ArrayList<Producer>(producerHub.getProducerMap(_context).values());
            for (int j = 0; j < producerArrayList.size(); j++) {
                Producer producer2 = producerArrayList.get(j);
                Log.w(Tag, producer2.toString());
            }

            // get specific producer from the hash
            Producer p1 = producerHub.getProducerMap(_context).get("P-2013-1");
            Log.w(Tag,"**** Found Newman Farm? => " + p1);

            // test orderhub...
            OrderHub orderHub = new OrderHub();
            ArrayList<Order> orderArrayList = new ArrayList<Order>(orderHub.getOrderMap(_context).values());
            for (int j = 0; j < orderArrayList.size(); j++) {
                Order order = orderArrayList.get(j);
                Log.w(Tag, order.toString());
            }

            // get specific oder from the hash
            Order order = orderHub.getOrderMap(_context).get("O-2014-2-2-7");
            Log.w(Tag,"**** Found Order? => " + order);

            // test buyerhub...
            BuyerHub buyerHub = new BuyerHub();
            ArrayList<Buyer> buyerArrayList = new ArrayList<Buyer>(buyerHub.getBuyerMap(_context).values());
            for (int j = 0; j < buyerArrayList.size(); j++) {
                Buyer buyer = buyerArrayList.get(j);
                Log.w(Tag, buyer.toString());
            }

            // get specific oder from the hash
            Buyer buyer = buyerHub.getBuyerMap(_context).get("B-2014-02");
            Log.w(Tag,"**** Found Benny's Bistro Buyer? => " + buyer);

            // test growerhub...
            // succeed
            growerHub.setItem(_context, producer, item, growerAgreementId);
            // fail with invalid category...
            item.setCategory("Foo");
            growerHub.setItem(_context, producer, item, growerAgreementId);
        } catch (Exception e) {
            // use email which will use smtp, so no need to retry, clean up, etc.
            Log.w(Tag, "e = " + e.toString());

            email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{ Hub.HUB_EMAIL_TO });
            email.putExtra(Intent.EXTRA_SUBJECT, Hub.HUB_EMAIL_SUBJECT);
            email.putExtra(Intent.EXTRA_TEXT, "We found a problem submitting your data to the Helena Local Hub... Click the send button for this email and we'll send the request to Helena Local for you... Next time you have a good network " +
                    "connection synchronize your email, then check your sent folder to make sure it went out.  Call us @ 406-219-1414 if you have questions or concerns.  " +
                    "\n\nDetails follow: \n-----------------" + producer.toEmail() + "\n+++++++++" + item.toEmail(growerAgreementId) + "\n-----------------");


            //need this to prompts email client only
            email.setType("message/rfc822");

        }

        return email;
    }

    @Override
    protected void onPostExecute(Intent intent) {
        super.onPostExecute(intent);

        if (intent != null) {
            _context.startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        }
    }
}