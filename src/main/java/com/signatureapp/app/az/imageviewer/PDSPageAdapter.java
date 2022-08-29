package com.signatureapp.app.az.imageviewer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.signatureapp.app.az.DocReader.PDSFragment;
import com.signatureapp.app.az.PDF.PDSPDFDocument;


public class PDSPageAdapter extends FragmentStatePagerAdapter {

    private PDSPDFDocument mDocument;

    public PDSPageAdapter(FragmentManager fragmentManager, PDSPDFDocument fASDocument) {
        super(fragmentManager);
        this.mDocument = fASDocument;
    }

    public int getCount() {
        return this.mDocument.getNumPages();
    }

    public Fragment getItem(int i) {
        return PDSFragment.newInstance(i);
    }

}
