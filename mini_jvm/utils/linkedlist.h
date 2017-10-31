//
// Created by gust on 2017/10/23.
//

#ifndef MINI_JVM_LINKEDLIST_H
#define MINI_JVM_LINKEDLIST_H

/**
 * @file list.h
 *
 * @brief Doubly-linked list.
 *
 * A doubly-linked list stores a collection of values.  Each entry in
 * the list (represented by a pointer a @ref ListEntry structure)
 * contains a link to the next entry and the previous entry.
 * It is therefore possible to iterate over entries in the list in either
 * direction.
 *
 * To create an empty list, create a new variable which is a pointer to
 * a @ref ListEntry structure, and initialise it to NULL.
 * To destroy an entire list, use @ref linkedlist_free.
 *
 * To add a value to a list, use @ref linkedlist_append or @ref linkedlist_prepend.
 *
 * To remove a value from a list, use @ref linkedlist_remove_entry or
 * @ref linkedlist_remove_data.
 *
 * To iterate over entries in a list, use @ref linkedlist_iterate to initialise
 * a @ref ListIterator structure, with @ref linkedlist_iter_next and
 * @ref linkedlist_iter_has_more to retrieve each value in turn.
 * @ref linkedlist_iter_remove can be used to remove the current entry.
 *
 * To access an entry in the list by index, use @ref linkedlist_nth_entry or
 * @ref linkedlist_nth_data.
 *
 * To modify data in the list use @ref linkedlist_set_data.
 *
 * To sort a list, use @ref linkedlist_sort.
 *
 */


#ifdef __cplusplus
extern "C" {
#endif

/**
 * Represents an entry in a doubly-linked list.  The empty list is
 * represented by a NULL pointer. To initialise a new doubly linked
 * list, simply create a variable of this type
 * containing a pointer to NULL.
 */

typedef void *ListValue;
typedef struct _ListEntry LinkedListEntry;
struct _ListEntry {
    ListValue data;
    LinkedListEntry *prev;
    LinkedListEntry *next;
};
typedef struct _LinkedList {
    LinkedListEntry *mNode;
    s64 length;
} LinkedList;


/**
 * A null @ref ListValue.
 */

#define LINKEDLIST_NULL ((void *) 0)

/**
 * Callback function used to compare values in a list when sorting.
 *
 * @param value1      The first value to compare.
 * @param value2      The second value to compare.
 * @return            A negative value if value1 should be sorted before
 *                    value2, a positive value if value1 should be sorted
 *                    after value2, zero if value1 and value2 are equal.
 */

typedef int (*ListCompareFunc)(ListValue value1, ListValue value2);


LinkedList *linkedlist_create();

void linkedlist_destory(LinkedList *list);

typedef int (*ListEqualFunc)(ListValue value1, ListValue value2);

void _linkedlist_free(LinkedListEntry *list);

LinkedListEntry *linkedlist_push_end(LinkedList *list, ListValue data);

LinkedListEntry *linkedlist_push_front(LinkedList *list, ListValue data);

LinkedListEntry *linkedlist_pop_front(LinkedList *list);

LinkedListEntry *linkedlist_pop_end(LinkedList *list);

LinkedListEntry *linkedlist_header(LinkedList *list);

LinkedListEntry *linkedlist_tail(LinkedList *list);

LinkedListEntry *linkedlist_prev(LinkedListEntry *listentry);

LinkedListEntry *linkedlist_next(LinkedListEntry *listentry);

ListValue linkedlist_data(LinkedListEntry *listentry);

void linkedlist_set_data(LinkedListEntry *listentry, ListValue value);


#ifdef __cplusplus
}
#endif
#endif //MINI_JVM_LINKEDLIST_H
