package com.signatureapp.app.az;

import static com.itextpdf.text.factories.RomanAlphabetFactory.getString;

import android.content.Context;
import android.content.res.Resources;

public class Companies {


    String id,name,representative;
    int industry;
    public static String[] industries;


    public Companies() {}

    public static void initializeIndustries(Context context) {
        industries = new String[]{
                context.getResources().getString(R.string.industry1),
                context.getResources().getString(R.string.industry2),
                context.getResources().getString(R.string.industry3),
                context.getResources().getString(R.string.industry4),
                context.getResources().getString(R.string.industry5),
                context.getResources().getString(R.string.industry6),
                context.getResources().getString(R.string.industry7),
                context.getResources().getString(R.string.industry8),
                context.getResources().getString(R.string.industry9),
                context.getResources().getString(R.string.industry10),
                context.getResources().getString(R.string.industry11),
                context.getResources().getString(R.string.industry12),
                context.getResources().getString(R.string.industry13),
                context.getResources().getString(R.string.industry14),
                context.getResources().getString(R.string.industry15),
                context.getResources().getString(R.string.industry16),
                context.getResources().getString(R.string.industry17),
        };
    }


    public void setId(String id) {
        this.id = id;
    }
    public void setName(String legal_name) {
        this.name = legal_name;
    }
    public void setRepresentative(String representative) {
        this.representative = representative;
    }
    public void setIndustry(int industry) {
        this.industry = industry;
    }

    public String getId() {
        return id;
    }
    public String getName() { return name; }
    public String getRepresentative() { return representative; }
    public int getIndustry() { return industry; }

}
