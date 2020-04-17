package sample;

import java.io.Serializable;

/**
 * Created by pro on 20.02.2020.
 */
public class OpticalConverter implements Serializable {

    private String mName;

    private int mDegereeRange;

    private int mEnergy;

    public OpticalConverter(String mName, int mDegereeRange, int mEnergy) {
        this.mName = mName;
        this.mDegereeRange = mDegereeRange;
        this.mEnergy = mEnergy;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public int getDegereeRange() {
        return mDegereeRange;
    }

    public void setDegereeRange(int mDegereeRange) {
        this.mDegereeRange = mDegereeRange;
    }

    public int getEnergy() {
        return mEnergy;
    }

    public void setEnergy(int mEnergy) {
        this.mEnergy = mEnergy;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
