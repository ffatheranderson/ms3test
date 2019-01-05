package sapronov.pavel.managementrestapi.utils;

import org.springframework.http.HttpMethod;

/**
 * Entity which implements setAllPrimitiveFieldsFrom
 *
 * @param <T>
 */
public interface PatchAndPutReady<T> {

    /**
     * Creates new instance of current entity and sets all (NULL fields too) primitive fields to it from that (except
     * id field).
     *
     * @param that
     */
    T patch(T that);

    /**
     * Creates new instance of current entity and sets all NOT NULL primitive fields to it from THAT (except id
     * field).
     *
     * @param that
     */
    T put(T that);

    /**
     * Creates NEW INSTANCE, based on THIS, but merges changes from updatedEntity in a PUT or PATCH manner respectively
     * to the method.
     *
     * @param updatedEntity
     * @param method
     * @return
     */
    default T patchOrPut(T updatedEntity, HttpMethod method) {
        if (method.equals(HttpMethod.PUT))
            return this.put(updatedEntity);
        else return this.patch(updatedEntity);
    }
}
