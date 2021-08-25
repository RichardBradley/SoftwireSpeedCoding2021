package q4;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.io.Resources;
import lombok.Value;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Q4Main {

    final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            new Q4Main().run();
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private void run() throws Exception {

        for (int week = 0; week < 4; week++) {

            MeetingRequestRoot data = objectMapper.readValue(
                    Resources.getResource("q4/Week" + week + ".json"),
                    MeetingRequestRoot.class);

            ImmutableListMultimap<Integer, MeetingRequest> meetingsByDay = Multimaps.index(data.meetingRequests, x -> x.day);

            int conflictCount = 0;
            int missedLunchCount = 0;
            for (int day = 0; day < 5; day++) {
                List<Pair<Integer, Integer>> conflicts = new ArrayList<>();
                ImmutableList<MeetingRequest> meetings = meetingsByDay.get(day);
                boolean[] hasMeetingByHour = new boolean[18];
                for (int i = 0; i < meetings.size(); i++) {
                    MeetingRequest meeting1 = meetings.get(i);

                    boolean isConflict = false;

                    for (int j = i + 1; j < meetings.size(); j++) {
                        MeetingRequest meeting2 = meetings.get(j);

                        if (meeting1.startTime >= meeting2.startTime && meeting1.startTime < meeting2.endTime()) {
                            // 1 starts inside 2
                            conflicts.add(new Pair<>(i, j));
                            conflictCount++;
                            isConflict = true;
                        } else if (meeting2.startTime >= meeting1.startTime && meeting2.startTime < meeting1.endTime()) {
                            // 2 starts inside 1
                            conflicts.add(new Pair<>(i, j));
                            conflictCount++;
                            isConflict = true;
                        }
                    }

                    if (!isConflict) {
                        meeting1.setTrueIfCovered(hasMeetingByHour);
                    }
                }

                if (!canHaveLunch(meetings, conflicts, 0, hasMeetingByHour)) {
                    missedLunchCount++;
                }
            }


            System.out.printf("Week %s: Conflicts: %s, Lunches missed: %s\n",
                    week,
                    conflictCount,
                    missedLunchCount);
        }
    }

    private boolean canHaveLunch(ImmutableList<MeetingRequest> meetings, List<Pair<Integer, Integer>> conflicts, int idx, boolean[] hasMeetingByHour) {
        if (idx >= conflicts.size()) {
            return (!hasMeetingByHour[7] && !hasMeetingByHour[8])
                    || (!hasMeetingByHour[8] && !hasMeetingByHour[9])
                    || (!hasMeetingByHour[9] && !hasMeetingByHour[10]);
        }
        // pick from the idx'th conflict, depth first search
        Pair<Integer, Integer> conflict = conflicts.get(idx);
        {
            boolean[] firstChosen = hasMeetingByHour.clone();
            meetings.get(conflict.getFirst()).setTrueIfCovered(firstChosen);
            if (canHaveLunch(meetings, conflicts, idx+1, firstChosen)) {
                return true;
            }
        }
        {
            boolean[] secondChosen = hasMeetingByHour.clone();
            meetings.get(conflict.getSecond()).setTrueIfCovered(secondChosen);
            if (canHaveLunch(meetings, conflicts, idx+1, secondChosen)) {
                return true;
            }
        }
        return false;
    }

    @Value
    static class MeetingRequestRoot {
        List<MeetingRequest> meetingRequests;
    }

    @Value
    static class MeetingRequest {
        int day;
        int startTime;
        int lengthInHalfHours;

        public int endTime() {
            return startTime + lengthInHalfHours;
        }

        public void setTrueIfCovered(boolean[] hasMeetingByHour) {
            for (int i = 0; i < lengthInHalfHours; i++) {
                hasMeetingByHour[startTime + i] = true;
            }
        }
    }
}
