package space.iseki.pefile;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ResourceWalker implements Iterable<ResourceWalker.@NotNull Entry> {
    private final @NotNull ResourceNode root;

    public ResourceWalker(@NotNull ResourceNode root) {
        this.root = Objects.requireNonNull(root);
    }

    @Override
    public @NotNull Iterator<@NotNull Entry> iterator() {
        Deque<Iterator<ResourceNode>> q = new ArrayDeque<>();
        Deque<ResourceNode> q2 = new ArrayDeque<>();
        q.add(root.peFile.listChildren(root).iterator());
        q2.add(root);
        return new AbstractIterator<>() {
            @Override
            protected void computeNext() {
                while (true) {
                    if (q.isEmpty()) {
                        end();
                        return;
                    }
                    if (q.size() > 32){
                        throw new PEFileException("Resource tree too deep");
                    }
                    var last = q.getLast();
                    if (last.hasNext()) {
                        var n = last.next();
                        var path = new ResourceNode[q2.size() + 1];
                        var i = 0;
                        for (var p : q2) {
                            path[i++] = p;
                        }
                        path[i] = n;
                        setNext(new Entry(path));
                        if (n.isDirectory()) {
                            q.addLast(n.peFile.listChildren(n).iterator());
                            q2.addLast(n);
                        }
                        return;
                    }
                    q.removeLast();
                    q2.removeLast();
                }
            }
        };
    }

    public static class Entry {
        final ResourceNode[] path;

        Entry(ResourceNode[] path) {
            this.path = path;
        }

        public @NotNull List<@NotNull ResourceNode> getPath() {
            return List.of(path);
        }

        public @NotNull ResourceNode getNode() {
            return path[path.length - 1];
        }

        public int getDepth() {
            return path.length;
        }
    }
}
