package com.hamusuke.threadr.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Either<L, R> {
    private Either() {
    }

    public static <L, R> Either<L, R> left(final L left) {
        return new Left<>(left);
    }

    public static <L, R> Either<L, R> right(final R right) {
        return new Right<>(right);
    }

    public abstract Either<L, R> ifLeftExists(final Consumer<? super L> leftConsumer);

    public abstract Either<L, R> ifRightExists(final Consumer<? super R> rightConsumer);

    public abstract Optional<L> getLeft();

    public abstract Optional<R> getRight();

    public static class Left<L, R> extends Either<L, R> {
        private final L left;

        public Left(L left) {
            this.left = left;
        }

        @Override
        public Either<L, R> ifLeftExists(Consumer<? super L> leftConsumer) {
            leftConsumer.accept(this.left);
            return this;
        }

        @Override
        public Either<L, R> ifRightExists(Consumer<? super R> rightConsumer) {
            return this;
        }

        @Override
        public Optional<L> getLeft() {
            return Optional.of(this.left);
        }

        @Override
        public Optional<R> getRight() {
            return Optional.empty();
        }

        @Override
        public String toString() {
            return "Left[" + this.left + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Left<?, ?> left = (Left<?, ?>) o;
            return Objects.equals(this.left, left.left);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.left);
        }
    }

    public static class Right<L, R> extends Either<L, R> {
        private final R right;

        public Right(R right) {
            this.right = right;
        }

        @Override
        public Either<L, R> ifLeftExists(Consumer<? super L> leftConsumer) {
            return this;
        }

        @Override
        public Either<L, R> ifRightExists(Consumer<? super R> rightConsumer) {
            rightConsumer.accept(this.right);
            return this;
        }

        @Override
        public Optional<L> getLeft() {
            return Optional.empty();
        }

        @Override
        public Optional<R> getRight() {
            return Optional.of(this.right);
        }

        @Override
        public String toString() {
            return "Right[" + this.right + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final Right<?, ?> right = (Right<?, ?>) o;
            return Objects.equals(this.right, right.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.right);
        }
    }
}
