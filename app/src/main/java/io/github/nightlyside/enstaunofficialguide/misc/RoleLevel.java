package io.github.nightlyside.enstaunofficialguide.misc;

public class RoleLevel {

    static public enum Level {
        ADMIN(1000),
        EDITOR(100),
        MEMBER(1);

        private Integer authLevel;

        Level(int authLevel) {
            this.authLevel = authLevel;
        }

        public boolean isAllowed(Level required) {
            return this.authLevel >= required.authLevel;
        }
    }

    static enum SpecialRight {
        EDIT_COLLOC,
        EDIT_USER,
        EDIT_ASSO
    }

    static public Level getLevelFromRole(String role) {
        switch (role) {
            case "admin":
                return Level.ADMIN;
            case "editeur":
                return Level.EDITOR;
            default:
                return Level.MEMBER;
        }
    }
}
