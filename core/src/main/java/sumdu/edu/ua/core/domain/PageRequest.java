package sumdu.edu.ua.core.domain;

public class PageRequest {
    private final int page;
    private final int size;
    private final String sort; // "id", "title", "author", "pubYear" + "asc"/"desc"

    public PageRequest(int page, int size) {
        this(page, size, "id desc");
    }

    public PageRequest(int page, int size, String sort) {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        this.page = page;
        this.size = size;
        this.sort = (sort != null && !sort.isBlank()) ? sort : "id desc";
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getSort() {
        return sort;
    }
}

