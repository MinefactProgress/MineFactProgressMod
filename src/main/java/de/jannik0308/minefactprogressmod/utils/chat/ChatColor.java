package de.jannik0308.minefactprogressmod.utils.chat;

public enum ChatColor {

    BLACK('0',"\u00A70"),
    DARK_BLUE('1',"\u00A71"),
    DARK_GREEN('2',"\u00A72"),
    DARK_AQUA('3',"\u00A73"),
    DARK_RED('4',"\u00A74"),
    DARK_PURPLE('5',"\u00A75"),
    GOLD('6',"\u00A76"),
    GRAY('7',"\u00A77"),
    DARK_GRAY('8',"\u00A78"),
    BLUE('9',"\u00A79"),
    GREEN('a',"\u00A7a"),
    AQUA('b',"\u00A7b"),
    RED('c',"\u00A7c"),
    LIGHT_PURPLE('d',"\u00A7d"),
    YELLOW('e',"\u00A7e"),
    WHITE('f',"\u00A7f"),
    RESET('r',"\u00A7r"),
    BOLD('l',"\u00A7l"),
    ITALIC('o',"\u00A7o"),
    UNDERLINE('n',"\u00A7n"),
    STRIKE('m',"\u00A7m"),
    OBFUSCATED('k',"\u00A7k");

    private final char code;
    private final String color;

    ChatColor(char code, String color) {
        this.code = code;
        this.color = color;
    }

    public ChatColor getByCode(char code) {
        for(ChatColor c : ChatColor.values()) {
            if(c.code == code) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return color;
    }
}
