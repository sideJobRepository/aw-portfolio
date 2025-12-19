"use client";

export interface Params {
  page: number;
  size: number;
}

type Props = {
  current: number;
  totalPages: number;
  onChange: (next: number) => void;
};

export default function Pagination({ current, totalPages, onChange }: Props) {
  if (!totalPages || totalPages < 1) return null;

  const windowSize = 6;

  const pages = (): (number | string)[] => {
    const last = totalPages - 1;

    if (totalPages <= windowSize + 1) {
      return Array.from({ length: totalPages }, (_, i) => i);
    }

    if (current <= windowSize - 1) {
      return [...Array.from({ length: windowSize }, (_, i) => i), "…", last];
    }

    if (current >= last - (windowSize - 1)) {
      const start = last - (windowSize - 1);
      return [
        0,
        "…",
        ...Array.from({ length: windowSize }, (_, i) => start + i),
      ];
    }

    const left = Math.max(0, current - 2);
    const right = Math.min(last, current + 2);

    return [0, "…", left, left + 1, current, right - 1, right, "…", last]
      .filter((v, i, arr) => typeof v === "string" || arr.indexOf(v) === i)
      .filter((v) => typeof v === "string" || (v >= 0 && v <= last));
  };

  return (
    <nav
      className="flex items-center justify-center gap-3"
      aria-label="pagination"
    >
      {/* Prev */}
      <button
        type="button"
        aria-label="previous page"
        disabled={current === 0}
        onClick={() => current > 0 && onChange(current - 1)}
        className={`px-2 text-lg ${
          current === 0
            ? "cursor-not-allowed text-gray-400"
            : "text-gray-600 hover:text-black"
        }`}
      >
        &lt;
      </button>

      {/* Page numbers */}
      <div className="flex items-center gap-2">
        {pages().map((p, i) =>
          typeof p === "number" ? (
            <button
              key={i}
              type="button"
              aria-current={p === current ? "page" : undefined}
              onClick={() => onChange(p)}
              className={`px-2 py-1 text-sm rounded ${
                p === current
                  ? "bg-blue-600 text-white"
                  : "text-gray-600 hover:bg-gray-100"
              }`}
            >
              {p + 1}
            </button>
          ) : (
            <span key={i} className="px-1 text-gray-400 select-none">
              …
            </span>
          ),
        )}
      </div>

      {/* Next */}
      <button
        type="button"
        aria-label="next page"
        disabled={current === totalPages - 1}
        onClick={() => current < totalPages - 1 && onChange(current + 1)}
        className={`px-2 text-lg ${
          current === totalPages - 1
            ? "cursor-not-allowed text-gray-400"
            : "text-gray-600 hover:text-black"
        }`}
      >
        &gt;
      </button>
    </nav>
  );
}
