package de.jannik0308.minefactprogressmod.utils.chat;

import java.util.List;

public class MessageBuilder {

    public static final String LINE = ChatColor.STRIKE + "                                                                 ";

    public static String getHelpMessage(String prefix, ChatColor colorLines, List<String> msgs, int maxPerPage, int page) {
        StringBuilder sb = new StringBuilder();
        int numMsgs = msgs.size();
        int numPages = 0;

        numPages = numMsgs % maxPerPage == 0 ? numMsgs / maxPerPage : (numMsgs / maxPerPage) + 1;

        if(page > numPages) {
            if(numPages == 1) {
                return prefix + ChatColor.RED + "There is only " + ChatColor.YELLOW + numPages + ChatColor.RED + " page";
            }
            return prefix + ChatColor.RED + "There are only " + ChatColor.YELLOW + numPages + ChatColor.RED + " pages";
        }
        sb.append(colorLines).append(LINE).append("\n");
        sb.append(prefix).append(ChatColor.GOLD + "Help " + ChatColor.GRAY + "(").append(page).append("/").append(numPages).append(ChatColor.GRAY + ")\n");
        for(int i = (page-1) * maxPerPage; i < ((page-1) * maxPerPage) + maxPerPage; i++) {
            try {
                sb.append(msgs.get(i)).append("\n");
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        sb.append(colorLines).append(LINE);
        return sb.toString();
    }

}
