FROM postgres:alpine3.18
ENV POSTGRES_DB chatwave
EXPOSE 5432
HEALTHCHECK --interval=5s --timeout=5s --start-period=5s --retries=5 CMD pg_isready -U postgres || exit 1