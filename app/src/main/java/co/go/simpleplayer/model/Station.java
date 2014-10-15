package co.go.simpleplayer.model;

/**
 * Created by roma on 21.09.2014.
 */
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Station implements Parcelable {
    private String title, thumbnailUrl;
    private int id, groupId;

    public Station() {
    }

    public Station(int id, int groupId, String name, String thumbnailUrl) {
        this.id = id;
        this.groupId = groupId;
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Station(Parcel in) {
        int[] ids = new int[] { 0, 0 };
        in.readIntArray(ids);
        this.id = ids[0];
        this.groupId = ids[1];
        String[] strings = new String[] { "", ""};
        in.readStringArray(strings);
        this.title = strings[0];
        this.thumbnailUrl = strings[1];
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupId() {
        return this.groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeIntArray(new int[]{getId(), getGroupId()});
        parcel.writeStringArray(new String[]{getTitle(), getThumbnailUrl()});
    }
}
