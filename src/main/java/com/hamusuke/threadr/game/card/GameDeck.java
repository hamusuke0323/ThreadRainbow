package com.hamusuke.threadr.game.card;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameDeck {
    private static final Set<Byte> ALL_CARDS = IntStream
            .rangeClosed(1, 100)
            .boxed()
            .map(Integer::byteValue)
            .collect(Collectors.toUnmodifiableSet());
    private final List<Byte> allCards;

    public GameDeck() {
        this.allCards = Lists.newArrayList(ALL_CARDS);
    }

    public byte pullCard(Random random) {
        return this.allCards.remove(random.nextInt(this.allCards.size()));
    }

    public void returnAllCards() {
        this.allCards.clear();
        this.allCards.addAll(ALL_CARDS);
    }
}
