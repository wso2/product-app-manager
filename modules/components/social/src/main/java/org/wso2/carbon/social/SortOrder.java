package org.wso2.carbon.social;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum SortOrder {
    NEWEST(new Comparator<Activity>() {
        @Override
        public int compare(Activity o1, Activity o2) {
            return o1.getTimestamp() - o2.getTimestamp();
        }
    }),
    OLDEST(new Comparator<Activity>() {
        @Override
        public int compare(Activity o1, Activity o2) {
            return o2.getTimestamp() - o1.getTimestamp();
        }
    }),
    POPULAR(new Comparator<Activity>() {
        @Override
        public int compare(Activity o1, Activity o2) {
//            o1.getLikeCount();
            return 0;
        }
    });
    private Comparator<? super Activity> comparator;

    SortOrder(Comparator<Activity> comparator) {
        this.comparator = comparator;
    }

    public void sort(List<Activity> activities) {
        Collections.sort(activities, comparator);
    }


}
