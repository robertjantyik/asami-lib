package hu.asami.dao.exceptions;

public class DaoMoreThenOneResultException extends DaoException{

    public DaoMoreThenOneResultException(String msg) {
        super(msg);
    }
}
