package com.example.journeytracking.Data;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Represents a table within the database. Room creates a table for each class that has @Entity annotation,
 * the fields in the class correspond to columns in the table. Therefore, the entity classes tend to be small
 * model classes that don’t contain any logic.
 */

@Entity(tableName = "ride_details")
public class RideDetails implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public double startLatitude;

    public double startLongitude;

    public double endLatitude;

    public double endLongitude;

    public double distanceCovered;

    public boolean isRideComplete;

    public String startTime;

    public String endTime;

    //If multiple constructors are suitable, add the @Ignore annotation to tell Room which should be used and which not.
    @Ignore
    public RideDetails(double endLatitude, double endLongitude, double distanceCovered, boolean isRideComplete, String endTime, long rideId) {

        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.distanceCovered = distanceCovered;
        this.isRideComplete = isRideComplete;
        this.endTime = endTime;
        this.id = rideId;
    }

    public RideDetails(double startLatitude, double startLongitude, boolean isRideComplete, String startTime) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.isRideComplete = isRideComplete;
        this.startTime = startTime;
    }

     RideDetails(Parcel in) {
        id = in.readLong();
        startLatitude = in.readDouble();
        startLongitude = in.readDouble();
        endLatitude = in.readDouble();
        endLongitude = in.readDouble();
        distanceCovered = in.readDouble();
        isRideComplete = in.readByte() != 0x00;
        startTime = in.readString();
        endTime = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(startLatitude);
        dest.writeDouble(startLongitude);
        dest.writeDouble(endLatitude);
        dest.writeDouble(endLongitude);
        dest.writeDouble(distanceCovered);
        dest.writeByte((byte) (isRideComplete ? 0x01 : 0x00));
        dest.writeString(startTime);
        dest.writeString(endTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<RideDetails> CREATOR = new Parcelable.Creator<RideDetails>() {
        @Override
        public RideDetails createFromParcel(Parcel in) {
            return new RideDetails(in);
        }

        @Override
        public RideDetails[] newArray(int size) {
            return new RideDetails[size];
        }
    };
}
