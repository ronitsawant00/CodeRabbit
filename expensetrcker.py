
import json, os, datetime

DB_FILE = "data.json"

def add_expense(amount, category):
    if os.path.exists(DB_FILE):
        with open(DB_FILE) as f:
            expenses=json.load(f)
    else:
        expenses=[]
    expenses.append({"amount": float(amount), "category": category, "date": str(datetime.date.today())})
    with open(DB_FILE,"w") as f:
        json.dump(expenses,f)
    print("Expense added!")

def get_total():
    if os.path.exists(DB_FILE):
        with open(DB_FILE) as f:
            expenses=json.load(f)
    else:
        expenses=[]
    total=0
    for e in expenses:
        total+=e["amount"]
    return total

def get_by_category(cat):
    """Returns a list of expenses that match the specified category.
    
    Filters and retrieves all expense records from the database that belong to the given category.

    Args:
        cat: The category to filter expenses by.

    Returns:
        list: A list of expense dictionaries matching the specified category.
    """
    if os.path.exists(DB_FILE):
        with open(DB_FILE) as f:
            expenses=json.load(f)
    else:
        expenses=[]
    filtered=[]
    for e in expenses:
        if e["category"]==cat:
            filtered.append(e)
    return filtered


if __name__=="__main__":
    while True:
        print("1.Add Expense\n2.View Total\n3.View by Category\n4.Exit")
        ch=int(input("Enter choice:"))
        if ch==1:
            amt=int(input("Amount:"))
            cat=input("Category:")
            add_expense(amt,cat)
        elif ch==2:
            print("Total:",get_total())
        elif ch==3:
            c=input("Enter category:")
            print(get_by_category(c))
        else:
            break
