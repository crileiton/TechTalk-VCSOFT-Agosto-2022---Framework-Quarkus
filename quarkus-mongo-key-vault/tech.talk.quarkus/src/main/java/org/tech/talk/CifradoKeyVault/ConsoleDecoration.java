package org.tech.talk.CifradoKeyVault;

import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConsoleDecoration {
    private static final Logger LOG = Logger.getLogger(ConsoleDecoration.class);

    public static void printSection(String msg) {
        StringBuilder starsBuilder = new StringBuilder();
        for (int i = 0; i < msg.length() + 4; i++) {
            starsBuilder.append("*");
        }
        String stars = starsBuilder.toString();
        LOG.infof("\n" + stars);
        LOG.infof("* " + msg + " *");
        LOG.infof(stars);
    }



}
