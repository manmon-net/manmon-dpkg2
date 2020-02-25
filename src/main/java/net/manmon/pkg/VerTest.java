package net.manmon.pkg;

public class VerTest {
    public static void main(String[] args) {
        String depends = "xserver-xorg (>= 1:7.7+19ubuntu7.1), libgl1-mesa-glx | libgl1, libgl1-mesa-dri, libglu1-mesa, xfonts-base (>= 1:1.0.0-1), x11-apps, x11-session-utils, x11-utils, x11-xkb-utils, x11-xserver-utils, xauth, xinit, xfonts-utils, xkb-data, xorg-docs-core, gnome-terminal | xterm | x-terminal-emulator, xinput";
        //depends="debconf (>= 0.5) | debconf-2.0 | a (<< 0.1) | b (= 0.2) | c (>> 0.5) | d (<= 0.6)";
        for (String s : depends.split(", ")) {
            //System.out.println(s);

            if (s.matches(".* \\| .*")) {
                for (String subRelation : s.split(" \\| ")) {
                    System.out.println("OR "+getDpkgRelation(subRelation));
                }
            } else {
                System.out.println(getDpkgRelation(s));
            }
        }
    }

    public static DpkgRelation getDpkgRelation(String s) {
        DpkgRelation relation = new DpkgRelation();
        relation.setRelationPackageName(s.replaceFirst(" \\(.*\\)$",""));
        if (s.matches("^.* \\(.*\\)$")) {
            relation.setRelationVersion(s.replaceFirst("^.*\\(.{1,2} ","").replaceFirst("\\)$",""));
            if (s.matches(".* \\(\\>\\= .*\\)$")) {
                relation.setRelation("GE");
            } else if (s.matches(".* \\(\\= .*\\)$")) {
                relation.setRelation("EQ");
            } else if (s.matches(".* \\(\\<\\= .*\\)$")) {
                relation.setRelation("LE");
            } else if (s.matches(".* \\(\\>\\> .*\\)$")) {
                relation.setRelation("GT");
            } else if (s.matches(".* \\(\\<\\< .*\\)$")) {
                relation.setRelation("LT");
            }
        }
        return relation;
    }
}
