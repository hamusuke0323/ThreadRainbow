package com.hamusuke.threadr.game.card;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hamusuke.threadr.server.game.card.ServerCard;
import com.hamusuke.threadr.server.network.ServerSpider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;

public class Cards {
    private static final Logger LOGGER = LogManager.getLogger();
    private final GameDeck deck;
    private final SecureRandom random = new SecureRandom();
    private final List<Integer> cards = Collections.synchronizedList(Lists.newArrayList());
    private int uncoveredIndex;

    public Cards(GameDeck deck) {
        this.deck = deck;
    }

    public void giveOutCards(List<ServerSpider> spiders) {
        this.cards.clear();
        this.uncoveredIndex = 0;
        spiders.forEach(spider -> {
            spider.takeCard(new ServerCard(spider, this.deck.pullCard(this.random)));
            this.cards.add(spider.getId());
        });
    }

    public synchronized void moveCard(int from, int to) throws IndexOutOfBoundsException {
        var v = this.cards.get(from);
        if (to < from) {
            this.cards.remove(from);
            this.cards.add(to, v);
        } else {
            this.cards.add(to + 1, v);
            this.cards.remove(from);
        }
    }

    @Nullable
    public UncoveredCard uncover() {
        if (this.uncoveredIndex >= this.cards.size()) {
            LOGGER.warn("Uncovering unknown card: {}", this.uncoveredIndex);
            return null;
        }

        int cur = this.uncoveredIndex;
        int ownerId = this.cards.get(this.uncoveredIndex++);
        int prevId = cur > 0 ? this.cards.get(cur - 1) : -1;
        boolean last = this.cards.size() <= this.uncoveredIndex;
        return new UncoveredCard(cur, ownerId, prevId, last);
    }

    public List<Integer> getCards() {
        return ImmutableList.copyOf(this.cards);
    }

    public void remove(ServerSpider spider) {
        int id = spider.getId();
        int index = this.cards.indexOf(id);
        if (index != -1 && index < this.uncoveredIndex) {
            this.uncoveredIndex--;
        }

        this.cards.remove((Integer) id);
    }

    public boolean hasCoveredCards() {
        return this.cards.size() > this.uncoveredIndex;
    }

    public record UncoveredCard(int uncoveredIndex, int ownerId, int prevId, boolean last) {
        public boolean isFirstCard() {
            return this.prevId < 0;
        }
    }
}
