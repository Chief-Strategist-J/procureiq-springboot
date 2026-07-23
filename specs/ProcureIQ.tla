---------------------------- MODULE ProcureIQ ----------------------------
(***************************************************************************)
(* Formal Specification for ProcureIQ Application Lifecycle & Workflow    *)
(* Features: Auth, Work Orders, Appointments, Jobs, Notifications, Audit  *)
(***************************************************************************)

EXTENDS Integers, Sequences, FiniteSets

CONSTANTS 
    Users,          \* Set of User IDs
    WorkTypes,      \* Set of Work Type IDs
    Statuses,       \* Set of Work Order Statuses: {"new", "in_progress", "completed", "cancelled"}
    MaxJobs         \* Maximum concurrent background jobs

VARIABLES
    userState,      \* Map of User -> {"unauthenticated", "authenticated"}
    workOrders,     \* Function mapping WorkOrderID -> [workType: WorkTypes, status: Statuses, priority: 1..5]
    appointments,   \* Function mapping AppointmentID -> [workOrder: WorkOrderID, status: {"scheduled", "completed"}]
    jobs,           \* Set of Active Job IDs
    notifications,  \* Sequence of Sent Notifications
    auditLogs       \* Sequence of Audit Log Hash Records

vars == <<userState, workOrders, appointments, jobs, notifications, auditLogs>>

(***************************************************************************)
(* Initial State Configuration                                             *)
(***************************************************************************)
Init ==
    /\ userState = [u \in Users |-> "unauthenticated"]
    /\ workOrders = [wo \in {} |-> {}]
    /\ appointments = [ap \in {} |-> {}]
    /\ jobs = {}
    /\ notifications = << >>
    /\ auditLogs = << >>

(***************************************************************************)
(* State Transitions & Actions                                            *)
(***************************************************************************)

\* User Authentication
AuthenticateUser(u) ==
    /\ userState[u] = "unauthenticated"
    /\ userState' = [userState EXCEPT ![u] = "authenticated"]
    /\ auditLogs' = Append(auditLogs, [event |-> "LOGIN_SUCCESS", user |-> u])
    /\ UNCHANGED <<workOrders, appointments, jobs, notifications>>

\* Work Order Lifecycle
CreateWorkOrder(u, id, wt, prio) ==
    /\ userState[u] = "authenticated"
    /\ id \notin DOMAIN workOrders
    /\ wt \in WorkTypes
    /\ prio \in 1..5
    /\ workOrders' = workOrders @@ (id |-> [workType |-> wt, status |-> "new", priority |-> prio])
    /\ auditLogs' = Append(auditLogs, [event |-> "WORK_ORDER_CREATED", id |-> id])
    /\ UNCHANGED <<userState, appointments, jobs, notifications>>

UpdateWorkOrderStatus(u, id, newStatus) ==
    /\ userState[u] = "authenticated"
    /\ id \in DOMAIN workOrders
    /\ newStatus \in Statuses
    /\ workOrders[id].status /= newStatus
    /\ workOrders' = [workOrders EXCEPT ![id].status = newStatus]
    /\ auditLogs' = Append(auditLogs, [event |-> "WORK_ORDER_UPDATED", id |-> id, status |-> newStatus])
    /\ UNCHANGED <<userState, appointments, jobs, notifications>>

\* Service Appointment Lifecycle
ScheduleAppointment(u, apId, woId) ==
    /\ userState[u] = "authenticated"
    /\ woId \in DOMAIN workOrders
    /\ apId \notin DOMAIN appointments
    /\ appointments' = appointments @@ (apId |-> [workOrder |-> woId, status |-> "scheduled"])
    /\ notifications' = Append(notifications, [recipient |-> u, type |-> "APPOINTMENT_SCHEDULED", id |-> apId])
    /\ auditLogs' = Append(auditLogs, [event |-> "APPOINTMENT_SCHEDULED", id |-> apId])
    /\ UNCHANGED <<userState, workOrders, jobs>>

\* Job Scheduler Lifecycle
ScheduleJob(jId) ==
    /\ Cardinality(jobs) < MaxJobs
    /\ jId \notin jobs
    /\ jobs' = jobs \cup {jId}
    /\ auditLogs' = Append(auditLogs, [event |-> "JOB_SCHEDULED", id |-> jId])
    /\ UNCHANGED <<userState, workOrders, appointments, notifications>>

CompleteJob(jId) ==
    /\ jId \in jobs
    /\ jobs' = jobs \ {jId}
    /\ auditLogs' = Append(auditLogs, [event |-> "JOB_COMPLETED", id |-> jId])
    /\ UNCHANGED <<userState, workOrders, appointments, notifications>>

(***************************************************************************)
(* Next State Relation                                                     *)
(***************************************************************************)
Next ==
    \/ \E u \in Users : AuthenticateUser(u)
    \/ \E u \in Users, id \in 1..100, wt \in WorkTypes, prio \in 1..5 : CreateWorkOrder(u, id, wt, prio)
    \/ \E u \in Users, id \in DOMAIN workOrders, s \in Statuses : UpdateWorkOrderStatus(u, id, s)
    \/ \E u \in Users, apId \in 1..100, woId \in DOMAIN workOrders : ScheduleAppointment(u, apId, woId)
    \/ \E jId \in 1..100 : ScheduleJob(jId)
    \/ \E jId \in jobs : CompleteJob(jId)

(***************************************************************************)
(* Safety & Liveness Invariants                                           *)
(***************************************************************************)

\* Safety Invariant: Appointments must always map to a valid Work Order
TypeInvariant ==
    /\ \A u \in Users : userState[u] \in {"unauthenticated", "authenticated"}
    /\ \A apId \in DOMAIN appointments : appointments[apId].workOrder \in DOMAIN workOrders
    /\ Cardinality(jobs) <= MaxJobs

\* Spec definition
Spec == Init /\ [][Next]_vars

=============================================================================
